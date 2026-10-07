package com.lankaride.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Multiton. There are exactly three report periods. Each name returns the
 * same shared object every time, instead of building a new one per request.
 */
public final class ReportWindow {

    private static final ReportWindow DAILY = new ReportWindow("daily");
    private static final ReportWindow MONTHLY = new ReportWindow("monthly");
    private static final ReportWindow ANNUAL = new ReportWindow("annual");

    private final String key;

    private ReportWindow(String key) {
        this.key = key;
    }

    public static ReportWindow of(String period) {
        if ("monthly".equals(period)) {
            return MONTHLY;
        }
        if ("annual".equals(period)) {
            return ANNUAL;
        }
        return DAILY;
    }

    public Span resolve(LocalDate day) {
        return switch (key) {
            case "monthly" -> new Span(
                    "monthly",
                    "Monthly — " + day.getMonth() + " " + day.getYear(),
                    day.withDayOfMonth(1).atStartOfDay(),
                    day.withDayOfMonth(1).plusMonths(1).atStartOfDay());
            case "annual" -> new Span(
                    "annual",
                    "Annual — " + day.getYear(),
                    LocalDate.of(day.getYear(), 1, 1).atStartOfDay(),
                    LocalDate.of(day.getYear() + 1, 1, 1).atStartOfDay());
            default -> new Span(
                    "daily",
                    "Daily — " + day,
                    day.atStartOfDay(),
                    day.plusDays(1).atStartOfDay());
        };
    }

    public record Span(String period, String label, LocalDateTime from, LocalDateTime to) {
    }
}
