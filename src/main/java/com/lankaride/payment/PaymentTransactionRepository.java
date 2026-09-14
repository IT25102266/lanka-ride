package com.lankaride.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByBookingIdOrderByCreatedAtAsc(Long bookingId);

    List<PaymentTransaction> findAllByOrderByCreatedAtDesc();

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p
            WHERE p.success = true
              AND p.type <> com.lankaride.common.PaymentType.REFUND
              AND p.createdAt >= :from
              AND p.createdAt < :to
            """)
    BigDecimal sumCollectedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p
            WHERE p.success = true
              AND p.type = com.lankaride.common.PaymentType.REFUND
              AND p.createdAt >= :from
              AND p.createdAt < :to
            """)
    BigDecimal sumRefundsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0) FROM PaymentTransaction p
            WHERE p.success = true
              AND p.type <> com.lankaride.common.PaymentType.REFUND
              AND p.booking.pickupBranch.id = :branchId
              AND p.createdAt >= :from
              AND p.createdAt < :to
            """)
    BigDecimal sumCollectedByBranchBetween(
            @Param("branchId") Long branchId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
