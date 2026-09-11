package com.lankaride.booking;

import com.lankaride.auth.UserAccount;
import com.lankaride.auth.UserAccountRepository;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.PaymentStatus;
import com.lankaride.common.VehicleStatus;
import com.lankaride.support.NotificationService;
import com.lankaride.vehicle.Branch;
import com.lankaride.vehicle.BranchRepository;
import com.lankaride.vehicle.Vehicle;
import com.lankaride.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(
            BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.ONGOING
    );

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final BranchRepository branchRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          VehicleRepository vehicleRepository,
                          BranchRepository branchRepository,
                          UserAccountRepository userAccountRepository,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.branchRepository = branchRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationService = notificationService;
    }

    public List<Booking> listAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Booking> listPending() {
        return bookingRepository.findByStatusOrderByCreatedAtAsc(BookingStatus.PENDING);
    }

    public List<Booking> listForCustomer(String username) {
        return bookingRepository.findByCustomerUsernameOrderByCreatedAtDesc(username);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    public long countPending() {
        return bookingRepository.countByStatus(BookingStatus.PENDING);
    }

    @Transactional
    public Booking create(String username, Long vehicleId, Long branchId,
                          LocalDate pickupDate, LocalDate returnDate) {
        if (pickupDate == null || returnDate == null || returnDate.isBefore(pickupDate)) {
            throw new IllegalArgumentException("Return date must be on or after pickup date");
        }
        if (pickupDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Pickup date cannot be in the past");
        }

        UserAccount customer = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE
                || vehicle.getStatus() == VehicleStatus.UNAVAILABLE
                || vehicle.getStatus() == VehicleStatus.RETIRED) {
            throw new IllegalArgumentException(
                    "Vehicle is not available for booking (status: " + vehicle.getStatus() + ")");
        }

        long overlaps = bookingRepository.countOverlapping(
                vehicleId, pickupDate, returnDate, BLOCKING_STATUSES);
        if (overlaps > 0) {
            throw new IllegalArgumentException(
                    "Vehicle already has a booking overlapping those dates");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setVehicle(vehicle);
        booking.setPickupBranch(branch);
        booking.setPickupDate(pickupDate);
        booking.setReturnDate(returnDate);
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setCreatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking approve(Long id, String staffUsername, String note) {
        Booking booking = getById(id);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending bookings can be approved");
        }
        Vehicle vehicle = booking.getVehicle();
        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE
                || vehicle.getStatus() == VehicleStatus.UNAVAILABLE
                || vehicle.getStatus() == VehicleStatus.RETIRED) {
            throw new IllegalArgumentException(
                    "Cannot approve — vehicle is currently " + vehicle.getStatus());
        }
        long overlaps = bookingRepository.countOverlapping(
                vehicle.getId(), booking.getPickupDate(), booking.getReturnDate(),
                List.of(BookingStatus.APPROVED, BookingStatus.ONGOING));
        if (overlaps > 0) {
            throw new IllegalArgumentException("Cannot approve — date conflict with another booking");
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setPaymentStatus(PaymentStatus.PENDING_PAYMENT);
        booking.setDecidedBy(staffUsername);
        booking.setDecisionReason(note);
        Booking saved = bookingRepository.save(booking);
        notificationService.email(saved.getCustomer().getEmail(),
                "Booking #" + saved.getId() + " approved",
                "Please pay the deposit and rental to confirm. Open Payments for this booking.");
        return saved;
    }

    @Transactional
    public Booking deny(Long id, String staffUsername, String reason) {
        Booking booking = getById(id);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending bookings can be denied");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A denial reason is required");
        }
        booking.setStatus(BookingStatus.DENIED);
        booking.setDecidedBy(staffUsername);
        booking.setDecisionReason(reason.trim());
        Booking saved = bookingRepository.save(booking);
        notificationService.email(saved.getCustomer().getEmail(),
                "Booking #" + saved.getId() + " denied",
                reason.trim());
        return saved;
    }

    @Transactional
    public Booking cancel(Long id, String username, boolean staff) {
        Booking booking = getById(id);
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Only pending or approved bookings can be cancelled");
        }
        if (!staff && !booking.getCustomer().getUsername().equals(username)) {
            throw new IllegalArgumentException("You can only cancel your own bookings");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setDecidedBy(username);
        return bookingRepository.save(booking);
    }
}
