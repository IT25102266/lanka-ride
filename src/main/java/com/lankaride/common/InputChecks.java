package com.lankaride.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Shared server-side checks for forms that are not bound to a single entity.
 */
public final class InputChecks {

    public static final Set<String> FUEL_LEVELS = Set.of("FULL", "3/4", "1/2", "1/4", "EMPTY");
    private static final Pattern LABEL = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9 .'\\-]{1,49}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9._]{2,29}$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern HTTP_URL = Pattern.compile("^https?://\\S{4,490}$");
    private static final BigDecimal MONEY_CAP = new BigDecimal("10000000");

    private InputChecks() {
    }

    public static String requiredText(String value, String label, int min, int max) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() < min || trimmed.length() > max) {
            throw new IllegalArgumentException(label + " must be " + min + "–" + max + " characters");
        }
        return trimmed;
    }

    public static String optionalText(String value, String label, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > max) {
            throw new IllegalArgumentException(label + " must be at most " + max + " characters");
        }
        return trimmed;
    }

    public static String label(String value, String field) {
        String trimmed = requiredText(value, field, 2, 50);
        if (!LABEL.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    field + " can only use letters, numbers, spaces, and hyphens");
        }
        return trimmed;
    }

    public static String username(String value) {
        String trimmed = requiredText(value, "Username", 3, 30);
        if (!USERNAME.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "Username must start with a letter and use only letters, numbers, dots, or underscores");
        }
        return trimmed;
    }

    public static String email(String value) {
        String trimmed = requiredText(value, "Email", 5, 120);
        if (!EMAIL.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        return trimmed;
    }

    public static String personName(String value) {
        String trimmed = requiredText(value, "Full name", 2, 80);
        if (!trimmed.matches("^[A-Za-z][A-Za-z .'-]{1,79}$")) {
            throw new IllegalArgumentException("Full name can only use letters, spaces, and hyphens");
        }
        return trimmed;
    }

    public static void password(String value) {
        if (value == null || value.length() < 8 || value.length() > 80) {
            throw new IllegalArgumentException("Password must be 8–80 characters");
        }
        if (value.isBlank()) {
            throw new IllegalArgumentException("Password must be 8–80 characters");
        }
    }

    public static void requiredMoney(BigDecimal value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " is required");
        }
        if (value.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException(label + " must be greater than zero");
        }
        if (value.compareTo(MONEY_CAP) > 0) {
            throw new IllegalArgumentException(label + " is too large");
        }
    }

    public static BigDecimal optionalMoney(BigDecimal value, String label) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException(label + " cannot be negative");
        }
        if (value.compareTo(MONEY_CAP) > 0) {
            throw new IllegalArgumentException(label + " is too large");
        }
        return value;
    }

    public static void tripDates(LocalDate pickup, LocalDate dropOff) {
        if (pickup == null || dropOff == null) {
            throw new IllegalArgumentException("Pickup and return dates are required");
        }
        if (pickup.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Pickup date cannot be in the past");
        }
        if (dropOff.isBefore(pickup)) {
            throw new IllegalArgumentException("Return date must be on or after the pickup date");
        }
    }

    public static void orderedDates(LocalDate start, LocalDate end, String endLabel) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException(endLabel + " cannot be before the start date");
        }
    }

    public static String photoUrl(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (!HTTP_URL.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Photo URL must start with http:// or https://");
        }
        return trimmed;
    }

    public static int mileage(Integer value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " is required");
        }
        if (value < 0 || value > 2_000_000) {
            throw new IllegalArgumentException(label + " must be between 0 and 2,000,000");
        }
        return value;
    }

    public static String fuelLevel(String value) {
        if (value == null || !FUEL_LEVELS.contains(value)) {
            throw new IllegalArgumentException("Fuel level must be FULL, 3/4, 1/2, 1/4, or EMPTY");
        }
        return value;
    }
}
