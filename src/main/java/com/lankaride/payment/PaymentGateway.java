package com.lankaride.payment;

/**
 * Strategy. Checkout asks a gateway to charge a card. The rest of payment
 * does not care which gateway that is. {@link DummyGateway} is the strategy
 * used in this project.
 */
public interface PaymentGateway {

    GatewayDecision charge(String method, String cardNumber, String holder, String expiry, String cvv);

    record GatewayDecision(boolean declined, String brand, String last4) {
    }
}
