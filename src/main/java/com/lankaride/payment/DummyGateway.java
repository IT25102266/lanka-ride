package com.lankaride.payment;

import org.springframework.stereotype.Component;
import java.time.YearMonth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sandbox card gateway. Card numbers and CVV are checked and then discarded.
 * A Visa number ending in 0002 is declined so the failure path can be demonstrated.
 */
@Component
public class DummyGateway implements PaymentGateway {

    private static final Pattern EXPIRY = Pattern.compile("^(0[1-9]|1[0-2])\\s*/\\s*(\\d{2}|\\d{4})$");
    private static final Pattern HOLDER = Pattern.compile("^[A-Za-z][A-Za-z .'\\-]{1,39}$");

    @Override
    public PaymentGateway.GatewayDecision charge(String method, String cardNumber, String holder, String expiry, String cvv) {
        String brand = normalizeMethod(method);
        String digits = digitsOnly(cardNumber);
        if (digits.length() < 13 || digits.length() > 19 || !luhn(digits)) {
            throw new IllegalArgumentException("Enter a valid card number");
        }
        if (!matchesBrand(brand, digits)) {
            throw new IllegalArgumentException("Card number does not match " + displayBrand(brand));
        }
        if (holder == null || !HOLDER.matcher(holder.trim()).matches()) {
            throw new IllegalArgumentException("Enter the name on the card");
        }
        parseExpiry(expiry);
        String code = cvv == null ? "" : cvv.trim();
        int cvvLength = "AMEX".equals(brand) ? 4 : 3;
        if (!code.matches("\\d{" + cvvLength + "}")) {
            throw new IllegalArgumentException("Enter a " + cvvLength + "-digit security code");
        }
        String last4 = digits.substring(digits.length() - 4);
        boolean declined = digits.endsWith("0002") || digits.endsWith("0000");
        return new PaymentGateway.GatewayDecision(declined, displayBrand(brand), last4);
    }

    private static String normalizeMethod(String method) {
        if (method == null) {
            throw new IllegalArgumentException("Choose Visa, Mastercard, or American Express");
        }
        return switch (method.trim().toUpperCase()) {
            case "VISA", "MASTERCARD", "AMEX" -> method.trim().toUpperCase();
            default -> throw new IllegalArgumentException("Choose Visa, Mastercard, or American Express");
        };
    }

    private static String displayBrand(String brand) {
        return switch (brand) {
            case "VISA" -> "Visa";
            case "MASTERCARD" -> "Mastercard";
            case "AMEX" -> "American Express";
            default -> brand;
        };
    }

    private static boolean matchesBrand(String brand, String digits) {
        return switch (brand) {
            case "VISA" -> digits.startsWith("4");
            case "MASTERCARD" -> digits.startsWith("5");
            case "AMEX" -> digits.startsWith("34") || digits.startsWith("37");
            default -> false;
        };
    }

    private static YearMonth parseExpiry(String expiry) {
        if (expiry == null) {
            throw new IllegalArgumentException("Enter the expiry as MM/YY");
        }
        Matcher matcher = EXPIRY.matcher(expiry.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Enter the expiry as MM/YY");
        }
        int year = Integer.parseInt(matcher.group(2));
        if (year < 100) {
            year += 2000;
        }
        YearMonth cardExpiry = YearMonth.of(year, Integer.parseInt(matcher.group(1)));
        if (cardExpiry.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("This card has expired");
        }
        return cardExpiry;
    }

    private static String digitsOnly(String cardNumber) {
        if (cardNumber == null) {
            return "";
        }
        return cardNumber.replaceAll("\\D", "");
    }

    private static boolean luhn(String digits) {
        int sum = 0;
        boolean alternate = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}
