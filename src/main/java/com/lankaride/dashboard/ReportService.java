package com.lankaride.dashboard;

import com.lankaride.booking.BookingRepository;
import com.lankaride.common.BookingStatus;
import com.lankaride.common.VehicleStatus;
import com.lankaride.payment.PaymentTransactionRepository;
import com.lankaride.vehicle.Branch;
import com.lankaride.vehicle.BranchRepository;
import com.lankaride.vehicle.Vehicle;
import com.lankaride.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final BranchRepository branchRepository;
    private final VehicleRepository vehicleRepository;

    public ReportService(BookingRepository bookingRepository,
                         PaymentTransactionRepository paymentTransactionRepository,
                         BranchRepository branchRepository,
                         VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.branchRepository = branchRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Map<String, Object> buildReport(String period, Long branchId, LocalDate refDate) {
        LocalDate day = refDate == null ? LocalDate.now() : refDate;
        LocalDateTime from;
        LocalDateTime to;
        String label;

        switch (period == null ? "daily" : period) {
            case "monthly" -> {
                from = day.withDayOfMonth(1).atStartOfDay();
                to = day.withDayOfMonth(1).plusMonths(1).atStartOfDay();
                label = "Monthly — " + day.getMonth() + " " + day.getYear();
            }
            case "annual" -> {
                from = LocalDate.of(day.getYear(), 1, 1).atStartOfDay();
                to = LocalDate.of(day.getYear() + 1, 1, 1).atStartOfDay();
                label = "Annual — " + day.getYear();
            }
            default -> {
                from = day.atStartOfDay();
                to = day.plusDays(1).atStartOfDay();
                label = "Daily — " + day;
                period = "daily";
            }
        }

        BigDecimal collected = branchId == null
                ? paymentTransactionRepository.sumCollectedBetween(from, to)
                : paymentTransactionRepository.sumCollectedByBranchBetween(branchId, from, to);
        BigDecimal refunds = paymentTransactionRepository.sumRefundsBetween(from, to);

        List<com.lankaride.booking.Booking> all = bookingRepository.findAllByOrderByCreatedAtDesc();
        long bookingsInPeriod = all.stream()
                .filter(b -> !b.getCreatedAt().isBefore(from) && b.getCreatedAt().isBefore(to))
                .filter(b -> branchId == null || b.getPickupBranch().getId().equals(branchId))
                .count();
        long completed = all.stream()
                .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
                .filter(b -> b.getReturnedAt() != null
                        && !b.getReturnedAt().isBefore(from)
                        && b.getReturnedAt().isBefore(to))
                .filter(b -> branchId == null || b.getPickupBranch().getId().equals(branchId))
                .count();

        List<Map<String, Object>> branchRows = new ArrayList<>();
        for (Branch branch : branchRepository.findAll()) {
            BigDecimal rev = paymentTransactionRepository.sumCollectedByBranchBetween(
                    branch.getId(), from, to);
            long fleetSize = vehicleRepository.findAll().stream()
                    .filter(v -> v.getBranch().getId().equals(branch.getId())
                            && v.getStatus() != VehicleStatus.RETIRED)
                    .count();
            long rented = all.stream()
                    .filter(b -> b.getPickupBranch().getId().equals(branch.getId()))
                    .filter(b -> b.getStatus() == BookingStatus.ONGOING
                            || b.getStatus() == BookingStatus.COMPLETED
                            || b.getStatus() == BookingStatus.APPROVED)
                    .filter(b -> !b.getCreatedAt().isBefore(from) && b.getCreatedAt().isBefore(to))
                    .count();
            BigDecimal utilization = fleetSize == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(rented * 100.0 / fleetSize).setScale(1, RoundingMode.HALF_UP);
            Map<String, Object> row = new HashMap<>();
            row.put("branch", branch);
            row.put("revenue", rev);
            row.put("fleetSize", fleetSize);
            row.put("bookings", rented);
            row.put("utilization", utilization);
            branchRows.add(row);
        }

        List<Map<String, Object>> locations = new ArrayList<>();
        for (Vehicle v : vehicleRepository.findAll()) {
            if (v.getStatus() == VehicleStatus.RETIRED) {
                continue;
            }
            Map<String, Object> loc = new HashMap<>();
            loc.put("vehicle", v);
            locations.add(loc);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("period", period);
        report.put("label", label);
        report.put("from", from);
        report.put("to", to);
        report.put("collected", collected);
        report.put("refunds", refunds);
        report.put("net", collected.subtract(refunds));
        report.put("bookingsInPeriod", bookingsInPeriod);
        report.put("completedInPeriod", completed);
        report.put("branchRows", branchRows);
        report.put("locations", locations);
        report.put("refDate", day);
        report.put("branchId", branchId);
        return report;
    }
}
