package com.lankaride.vehicle;

import com.lankaride.booking.BookingRepository;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import com.lankaride.common.VehicleStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VehicleService {

    private static final List<BookingStatus> BLOCKING_STATUSES = List.of(
            BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.ONGOING
    );

    private final VehicleRepository vehicleRepository;
    private final BranchRepository branchRepository;
    private final BookingRepository bookingRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          BranchRepository branchRepository,
                          BookingRepository bookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.branchRepository = branchRepository;
        this.bookingRepository = bookingRepository;
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
        vehicleRepository.findByRegistrationNumber(incoming.getRegistrationNumber())
                .filter(v -> !v.getId().equals(id))
                .ifPresent(v -> {
                    throw new IllegalArgumentException("Registration number already exists");
                });

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        existing.setRegistrationNumber(incoming.getRegistrationNumber());
        existing.setCategory(incoming.getCategory());
        existing.setBrand(incoming.getBrand());
        existing.setModel(incoming.getModel());
        existing.setSeats(incoming.getSeats());
        existing.setGearbox(incoming.getGearbox());
        existing.setFuelType(incoming.getFuelType());
        existing.setFeatures(incoming.getFeatures());
        existing.setPhotoUrl(incoming.getPhotoUrl());
        existing.setPricePerDay(incoming.getPricePerDay());
        existing.setDepositAmount(incoming.getDepositAmount());
        existing.setStatus(incoming.getStatus());
        existing.setCurrentLocation(incoming.getCurrentLocation());
        existing.setBranch(branch);
        return vehicleRepository.save(existing);
    }

    @Transactional
    public void retire(Long id) {
        Vehicle vehicle = getById(id);
        vehicle.setStatus(VehicleStatus.RETIRED);
        vehicleRepository.save(vehicle);
    }
}
