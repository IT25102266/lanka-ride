package com.lankaride.fleet;

import com.lankaride.booking.Booking;
import com.lankaride.booking.BookingRepository;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.MaintenanceStatus;
import com.lankaride.common.VehicleStatus;
import com.lankaride.vehicle.Vehicle;
import com.lankaride.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES = List.of(
            BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.ONGOING
    );

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    public MaintenanceService(MaintenanceRecordRepository maintenanceRecordRepository,
                              VehicleRepository vehicleRepository,
                              BookingRepository bookingRepository) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<MaintenanceRecord> listAll() {
        return maintenanceRecordRepository.findAllByOrderByServiceDateDesc();
    }

    public List<MaintenanceRecord> listByVehicle(Long vehicleId) {
        return maintenanceRecordRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId);
    }

    public MaintenanceRecord getById(Long id) {
        return maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found"));
    }

    public List<Booking> findConflictingBookings(Long vehicleId) {
        return bookingRepository.findFutureOrActiveForVehicle(
                vehicleId, LocalDate.now(), ACTIVE_BOOKING_STATUSES);
    }

    @Transactional
    public MaintenanceRecord create(Long vehicleId, MaintenanceRecord incoming, boolean force) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        List<Booking> conflicts = findConflictingBookings(vehicleId);
        if (!conflicts.isEmpty() && !force) {
            throw new IllegalStateException(
                    "Vehicle has " + conflicts.size()
                            + " future/active booking(s). Confirm to take it offline for maintenance.");
        }

        incoming.setId(null);
        incoming.setVehicle(vehicle);
        if (incoming.getStatus() == null) {
            incoming.setStatus(MaintenanceStatus.OPEN);
        }
        if (incoming.getServiceDate() == null) {
            incoming.setServiceDate(LocalDate.now());
        }
        MaintenanceRecord saved = maintenanceRecordRepository.save(incoming);
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicleRepository.save(vehicle);
        return saved;
    }

    @Transactional
    public MaintenanceRecord update(Long id, MaintenanceRecord incoming) {
        MaintenanceRecord existing = getById(id);
        if (existing.getStatus() == MaintenanceStatus.CLOSED) {
            throw new IllegalArgumentException("Closed records cannot be edited");
        }
        existing.setServiceType(incoming.getServiceType());
        existing.setServiceDate(incoming.getServiceDate());
        existing.setEstimatedCompletionDate(incoming.getEstimatedCompletionDate());
        existing.setEstimatedCost(incoming.getEstimatedCost());
        existing.setDescription(incoming.getDescription());
        existing.setMechanicsAssigned(incoming.getMechanicsAssigned());
        existing.setStatus(incoming.getStatus() == MaintenanceStatus.CLOSED
                ? MaintenanceStatus.IN_PROGRESS
                : incoming.getStatus());
        return maintenanceRecordRepository.save(existing);
    }

    @Transactional
    public MaintenanceRecord close(Long id, java.math.BigDecimal finalCost) {
        MaintenanceRecord existing = getById(id);
        if (existing.getStatus() == MaintenanceStatus.CLOSED) {
            return existing;
        }
        existing.setStatus(MaintenanceStatus.CLOSED);
        existing.setCompletionDate(LocalDate.now());
        existing.setFinalCost(finalCost);
        MaintenanceRecord saved = maintenanceRecordRepository.save(existing);

        Long vehicleId = existing.getVehicle().getId();
        long openCount = maintenanceRecordRepository.countByVehicleIdAndStatusIn(
                vehicleId, List.of(MaintenanceStatus.OPEN, MaintenanceStatus.IN_PROGRESS));
        if (openCount == 0) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }
        return saved;
    }
}
