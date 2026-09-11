package com.lankaride.booking;

import com.lankaride.common.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerUsernameOrderByCreatedAtDesc(String username);

    List<Booking> findByStatusOrderByCreatedAtAsc(BookingStatus status);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    @Query("""
            SELECT COUNT(b) FROM Booking b
            WHERE b.vehicle.id = :vehicleId
              AND b.status IN :statuses
              AND b.pickupDate <= :endDate
              AND b.returnDate >= :startDate
            """)
    long countOverlapping(
            @Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<BookingStatus> statuses
    );

    @Query("""
            SELECT b FROM Booking b
            WHERE b.vehicle.id = :vehicleId
              AND b.status IN :statuses
              AND b.returnDate >= :fromDate
            ORDER BY b.pickupDate
            """)
    List<Booking> findFutureOrActiveForVehicle(
            @Param("vehicleId") Long vehicleId,
            @Param("fromDate") LocalDate fromDate,
            @Param("statuses") List<BookingStatus> statuses
    );
}
