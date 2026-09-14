package com.lankaride.vehicle;

import com.lankaride.common.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.lankaride.common.FuelType;
import com.lankaride.common.GearboxType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    long countByStatus(VehicleStatus status);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.status <> com.lankaride.common.VehicleStatus.RETIRED
              AND (:category IS NULL OR :category = '' OR LOWER(v.category) = LOWER(:category))
              AND (:gearbox IS NULL OR v.gearbox = :gearbox)
              AND (:fuelType IS NULL OR v.fuelType = :fuelType)
              AND (:branchId IS NULL OR v.branch.id = :branchId)
              AND (:minPrice IS NULL OR v.pricePerDay >= :minPrice)
              AND (:maxPrice IS NULL OR v.pricePerDay <= :maxPrice)
              AND (:status IS NULL OR v.status = :status)
            ORDER BY v.brand, v.model
            """)
    List<Vehicle> search(
            @Param("category") String category,
            @Param("gearbox") GearboxType gearbox,
            @Param("fuelType") FuelType fuelType,
            @Param("branchId") Long branchId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") VehicleStatus status
    );
}
