package com.lankaride.vehicle;

import com.lankaride.booking.Booking;
import com.lankaride.booking.BookingRepository;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.common.InputChecks;
import com.lankaride.common.VehicleStatus;
import com.lankaride.fleet.MaintenanceRecordRepository;
import com.lankaride.payment.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(
            BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.ONGOING
    );

    private final VehicleRepository vehicleRepository;
    private final BranchRepository branchRepository;
    private final BookingRepository bookingRepository;
    private final BranchTransferRepository branchTransferRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          BranchRepository branchRepository,
                          BookingRepository bookingRepository,
                          BranchTransferRepository branchTransferRepository,
                          MaintenanceRecordRepository maintenanceRecordRepository,
                          PaymentTransactionRepository paymentTransactionRepository) {
        this.vehicleRepository = vehicleRepository;
        this.branchRepository = branchRepository;
        this.bookingRepository = bookingRepository;
        this.branchTransferRepository = branchTransferRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    public List<Branch> listBranches() {
        return branchRepository.findAll();
    }

    public List<Vehicle> search(String category, GearboxType gearbox, FuelType fuelType,
                                Long branchId, BigDecimal minPrice, BigDecimal maxPrice,
                                VehicleStatus status) {
        return search(category, gearbox, fuelType, branchId, minPrice, maxPrice, status, null, null, false);
    }

    public List<Vehicle> search(String category, GearboxType gearbox, FuelType fuelType,
                                Long branchId, BigDecimal minPrice, BigDecimal maxPrice,
                                VehicleStatus status, LocalDate pickupDate, LocalDate returnDate,
                                boolean availableOnly) {
        VehicleStatus effectiveStatus = status;
        if (availableOnly && effectiveStatus == null) {
            effectiveStatus = VehicleStatus.AVAILABLE;
        }
        List<Vehicle> vehicles = vehicleRepository.search(
                category, gearbox, fuelType, branchId, minPrice, maxPrice, effectiveStatus);

        if (pickupDate != null && returnDate != null && !returnDate.isBefore(pickupDate)) {
            return vehicles.stream()
                    .filter(v -> {
                        if (availableOnly && v.getStatus() != VehicleStatus.AVAILABLE) {
                            return false;
                        }
                        if (v.getStatus() == VehicleStatus.MAINTENANCE
                                || v.getStatus() == VehicleStatus.UNAVAILABLE
                                || v.getStatus() == VehicleStatus.RETIRED) {
                            return false;
                        }
                        long overlaps = bookingRepository.countOverlapping(
                                v.getId(), pickupDate, returnDate, BLOCKING_STATUSES);
                        return overlaps == 0;
                    })
                    .toList();
        }

        if (availableOnly) {
            return vehicles.stream()
                    .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                    .toList();
        }
        return vehicles;
    }

    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + id));
    }

    @Transactional
    public Vehicle create(Vehicle vehicle, Long branchId) {
        normalize(vehicle);
        if (vehicleRepository.existsByRegistrationNumber(vehicle.getRegistrationNumber())) {
            throw new IllegalArgumentException("Registration number already exists");
        }
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
        vehicle.setBranch(branch);
        if (vehicle.getCurrentLocation() == null || vehicle.getCurrentLocation().isBlank()) {
            vehicle.setCurrentLocation(branch.getName());
        }
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle update(Long id, Vehicle incoming, Long branchId) {
        Vehicle existing = getById(id);
        normalize(incoming);
        vehicleRepository.findByRegistrationNumber(incoming.getRegistrationNumber())
                .filter(v -> !v.getId().equals(id))
                .ifPresent(v -> {
                    throw new IllegalArgumentException("Registration number already exists");
                });

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        VehicleBuilder.from(incoming).applyTo(existing);
        existing.setBranch(branch);
        return vehicleRepository.save(existing);
    }

    public List<BranchTransfer> listTransfers(Long vehicleId) {
        return branchTransferRepository.findByVehicleIdOrderByTransferredAtDesc(vehicleId);
    }

    @Transactional
    public BranchTransfer transfer(Long vehicleId, Long toBranchId, String actor, String note) {
        Vehicle vehicle = getById(vehicleId);
        if (vehicle.getStatus() == VehicleStatus.RETIRED) {
            throw new IllegalArgumentException("Retired vehicles cannot be transferred");
        }
        Branch toBranch = branchRepository.findById(toBranchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
        if (vehicle.getBranch().getId().equals(toBranch.getId())) {
            throw new IllegalArgumentException("Vehicle is already at " + toBranch.getName());
        }

        BranchTransfer transfer = new BranchTransfer();
        transfer.setVehicle(vehicle);
        transfer.setFromBranch(vehicle.getBranch());
        transfer.setToBranch(toBranch);
        transfer.setFromLocation(vehicle.getCurrentLocation());
        transfer.setTransferredBy(actor);
        transfer.setNote(note == null ? "" : note.trim());
        transfer.setTransferredAt(LocalDateTime.now());

        vehicle.setBranch(toBranch);
        vehicle.setCurrentLocation(toBranch.getName());
        vehicleRepository.save(vehicle);
        return branchTransferRepository.save(transfer);
    }

    @Transactional
    public void retire(Long id) {
        Vehicle vehicle = getById(id);
        vehicle.setStatus(VehicleStatus.RETIRED);
        vehicleRepository.save(vehicle);
    }

    /**
     * Removes the vehicle and every row that belongs to it: bookings, payments,
     * maintenance, and the transfer log.
     */
    @Transactional
    public void deletePermanently(Long id) {
        Vehicle vehicle = getById(id);
        for (Booking booking : bookingRepository.findByVehicleId(id)) {
            paymentTransactionRepository.deleteByBookingId(booking.getId());
        }
        paymentTransactionRepository.flush();
        bookingRepository.deleteAll(bookingRepository.findByVehicleId(id));
        bookingRepository.flush();
        maintenanceRecordRepository.deleteByVehicleId(id);
        branchTransferRepository.deleteByVehicleId(id);
        maintenanceRecordRepository.flush();
        vehicleRepository.delete(vehicle);
    }

    private void normalize(Vehicle vehicle) {
        vehicle.setRegistrationNumber(InputChecks.requiredText(
                vehicle.getRegistrationNumber(), "Registration number", 3, 20).toUpperCase());
        vehicle.setCategory(InputChecks.label(vehicle.getCategory(), "Category"));
        vehicle.setBrand(InputChecks.label(vehicle.getBrand(), "Brand"));
        vehicle.setModel(InputChecks.label(vehicle.getModel(), "Model"));
        if (vehicle.getSeats() < 1 || vehicle.getSeats() > 20) {
            throw new IllegalArgumentException("Seats must be between 1 and 20");
        }
        InputChecks.requiredMoney(vehicle.getPricePerDay(), "Price per day");
        InputChecks.requiredMoney(vehicle.getDepositAmount(), "Deposit");
        vehicle.setFeatures(InputChecks.optionalText(vehicle.getFeatures(), "Features", 500));
        vehicle.setCurrentLocation(InputChecks.optionalText(vehicle.getCurrentLocation(), "Location", 100));
        vehicle.setPhotoUrl(InputChecks.photoUrl(vehicle.getPhotoUrl()));
        if (vehicle.getGearbox() == null || vehicle.getFuelType() == null || vehicle.getStatus() == null) {
            throw new IllegalArgumentException("Gearbox, fuel type, and status are required");
        }
    }
}
