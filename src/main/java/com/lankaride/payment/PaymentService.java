package com.lankaride.payment;

import com.lankaride.booking.Booking;
import com.lankaride.booking.BookingRepository;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.InputChecks;
import com.lankaride.common.PaymentStatus;
import com.lankaride.common.PaymentType;
import com.lankaride.fleet.MaintenanceService;
import com.lankaride.support.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Map<String, Integer> FUEL_RANK = Map.of(
            "EMPTY", 0,
            "1/4", 1,
            "1/2", 2,
            "3/4", 3,
            "FULL", 4
    );

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final MaintenanceService maintenanceService;

    public PaymentService(PaymentTransactionRepository paymentTransactionRepository,
                          BookingRepository bookingRepository,
                          NotificationService notificationService,
                          MaintenanceService maintenanceService) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
        this.maintenanceService = maintenanceService;
    }

    public List<PaymentTransaction> listAll() {
        return paymentTransactionRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<PaymentTransaction> listForBooking(Long bookingId) {
        return paymentTransactionRepository.findByBookingIdOrderByCreatedAtAsc(bookingId);
    }

    public BigDecimal rentalDaysAmount(Booking booking) {
        long days = ChronoUnit.DAYS.between(booking.getPickupDate(), booking.getReturnDate()) + 1;
        if (days < 1) {
            days = 1;
        }
        return booking.getVehicle().getPricePerDay().multiply(BigDecimal.valueOf(days));
    }

    public BigDecimal totalDueForApproval(Booking booking) {
        return booking.getVehicle().getDepositAmount().add(rentalDaysAmount(booking));
    }

    @Transactional
    public Booking payApprovedBooking(Long bookingId, String username, boolean failGateway) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getCustomer().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only pay for your own bookings");
        }
        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved bookings can be paid");
        }
        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("Booking is already paid");
        }

        BigDecimal deposit = booking.getVehicle().getDepositAmount();
        BigDecimal rental = rentalDaysAmount(booking);
        String gatewayRef = "MOCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (failGateway) {
            saveTx(booking, PaymentType.DEPOSIT, deposit, false, gatewayRef + "-FAIL",
                    "Sandbox decline", username);
            notificationService.email(
                    booking.getCustomer().getEmail(),
                    "Payment declined — booking #" + booking.getId(),
                    "The sandbox gateway declined the deposit. You can try again.");
            throw new IllegalArgumentException("Payment gateway declined the transaction (sandbox fail)");
        }

        saveTx(booking, PaymentType.DEPOSIT, deposit, true, gatewayRef + "-D", "Booking deposit", username);
        saveTx(booking, PaymentType.RENTAL, rental, true, gatewayRef + "-R", "Rental fee", username);

        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setInvoiceNumber("INV-" + booking.getId() + "-" + LocalDate.now().getYear());
        booking.setStatus(BookingStatus.ONGOING);
        Booking saved = bookingRepository.save(booking);

        notificationService.email(
                saved.getCustomer().getEmail(),
                "Payment received — booking #" + saved.getId(),
                "Deposit LKR " + deposit + " and rental LKR " + rental
                        + " received. Invoice " + saved.getInvoiceNumber() + ".");
        return saved;
    }

    @Transactional
    public Booking recordPickup(Long bookingId, Integer mileage, String fuelLevel) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalArgumentException("Booking must be paid before pickup");
        }
        if (booking.getStatus() != BookingStatus.ONGOING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Booking is not ready for pickup");
        }
        booking.setPickupMileage(InputChecks.mileage(mileage, "Pickup mileage"));
        booking.setPickupFuelLevel(InputChecks.fuelLevel(fuelLevel));
        booking.setStatus(BookingStatus.ONGOING);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking completeReturn(Long bookingId, Integer returnMileage, String returnFuel,
                                  BigDecimal lateFee, BigDecimal damageCharge, String staffUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (booking.getStatus() != BookingStatus.ONGOING) {
            throw new IllegalArgumentException("Only ongoing rentals can be returned");
        }
        int checkedMileage = InputChecks.mileage(returnMileage, "Return mileage");
        if (booking.getPickupMileage() != null && checkedMileage < booking.getPickupMileage()) {
            throw new IllegalArgumentException("Return mileage cannot be less than pickup mileage");
        }

        booking.setReturnMileage(checkedMileage);
        booking.setReturnFuelLevel(InputChecks.fuelLevel(returnFuel));
        booking.setReturnedAt(LocalDateTime.now());
        booking.setLateFeeAmount(InputChecks.optionalMoney(lateFee, "Late fee"));
        booking.setDamageChargeAmount(InputChecks.optionalMoney(damageCharge, "Damage charge"));

        if (booking.getLateFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
            saveTx(booking, PaymentType.LATE_FEE, booking.getLateFeeAmount(), true,
                    "LATE-" + booking.getId(), "Late return fee", staffUser);
        }
        if (booking.getDamageChargeAmount().compareTo(BigDecimal.ZERO) > 0) {
            saveTx(booking, PaymentType.DAMAGE, booking.getDamageChargeAmount(), true,
                    "DMG-" + booking.getId(), "Damage charge", staffUser);
        }

        String discrepancy = describeDiscrepancy(booking);
        booking.setDiscrepancyFlag(discrepancy != null);
        booking.setDiscrepancyNote(discrepancy);
        booking.setStatus(BookingStatus.COMPLETED);
        Booking saved = bookingRepository.save(booking);
        if (discrepancy != null) {
            maintenanceService.openInspectionFromReturn(saved, discrepancy);
            notificationService.email(
                    saved.getCustomer().getEmail(),
                    "Return check — booking #" + saved.getId(),
                    "A mileage or fuel discrepancy was recorded: " + discrepancy
                            + ". Fleet has opened an inspection.");
        }
        notificationService.email(
                saved.getCustomer().getEmail(),
                "Vehicle returned — booking #" + saved.getId(),
                "Return recorded. Late fee LKR " + saved.getLateFeeAmount()
                        + ", damage LKR " + saved.getDamageChargeAmount() + ".");
        return saved;
    }

    @Transactional
    public Booking refund(Long bookingId, String reason, String staffUser) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (booking.getPaymentStatus() != PaymentStatus.PAID
                && booking.getPaymentStatus() != PaymentStatus.REFUNDED) {
            throw new IllegalArgumentException("No paid amount to refund");
        }
        if (booking.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new IllegalArgumentException("Already refunded");
        }
        if (booking.getStatus() != BookingStatus.CANCELLED
                && booking.getStatus() != BookingStatus.DENIED
                && booking.getStatus() != BookingStatus.APPROVED
                && booking.getStatus() != BookingStatus.ONGOING) {
            throw new IllegalArgumentException("Refund not allowed for status " + booking.getStatus());
        }

        BigDecimal refundable = listForBooking(bookingId).stream()
                .filter(PaymentTransaction::isSuccess)
                .filter(p -> p.getType() != PaymentType.REFUND)
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (refundable.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nothing to refund");
        }

        reason = InputChecks.requiredText(reason, "Refund reason", 3, 300);
        saveTx(booking, PaymentType.REFUND, refundable, true,
                "REF-" + booking.getId(), reason, staffUser);
        booking.setPaymentStatus(PaymentStatus.REFUNDED);
        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.ONGOING) {
            booking.setStatus(BookingStatus.CANCELLED);
        }
        Booking saved = bookingRepository.save(booking);
        notificationService.email(
                saved.getCustomer().getEmail(),
                "Refund processed — booking #" + saved.getId(),
                "Refund of LKR " + refundable + " issued. " + reason);
        return saved;
    }

    String describeDiscrepancy(Booking booking) {
        List<String> notes = new ArrayList<>();
        Integer pickupMileage = booking.getPickupMileage();
        Integer returnMileage = booking.getReturnMileage();
        if (pickupMileage != null && returnMileage != null) {
            int driven = returnMileage - pickupMileage;
            long days = ChronoUnit.DAYS.between(booking.getPickupDate(), booking.getReturnDate());
            if (days < 1) {
                days = 1;
            }
            if (driven > days * 500) {
                notes.add("High mileage: " + driven + " km over " + days + " day(s)");
            }
        }
        int pickupFuel = fuelRank(booking.getPickupFuelLevel());
        int returnFuel = fuelRank(booking.getReturnFuelLevel());
        if (pickupFuel >= 0 && returnFuel >= 0 && returnFuel + 1 < pickupFuel) {
            notes.add("Fuel drop from " + booking.getPickupFuelLevel()
                    + " to " + booking.getReturnFuelLevel());
        }
        if (notes.isEmpty()) {
            return null;
        }
        return String.join("; ", notes);
    }

    private int fuelRank(String level) {
        if (level == null) {
            return -1;
        }
        return FUEL_RANK.getOrDefault(level.trim().toUpperCase(Locale.ROOT), -1);
    }

    private void saveTx(Booking booking, PaymentType type, BigDecimal amount,
                        boolean success, String ref, String note, String actor) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setBooking(booking);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setSuccess(success);
        tx.setGatewayReference(ref);
        tx.setNote(note);
        tx.setActorUsername(actor);
        paymentTransactionRepository.save(tx);
    }
}
