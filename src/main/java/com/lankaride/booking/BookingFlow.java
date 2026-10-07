package com.lankaride.booking;

import com.lankaride.common.BookingStatus;

/**
 * State. Each booking status is one state, and that state knows which
 * staff actions are legal. The service still does the date and vehicle
 * checks; this object only answers "may this status change this way?"
 */
public enum BookingFlow {
    PENDING(true, true, true),
    APPROVED(false, false, true),
    DENIED(false, false, false),
    ONGOING(false, false, false),
    COMPLETED(false, false, false),
    CANCELLED(false, false, false);

    private final boolean canApprove;
    private final boolean canDeny;
    private final boolean canCancel;

    BookingFlow(boolean canApprove, boolean canDeny, boolean canCancel) {
        this.canApprove = canApprove;
        this.canDeny = canDeny;
        this.canCancel = canCancel;
    }

    public static BookingFlow of(BookingStatus status) {
        return BookingFlow.valueOf(status.name());
    }

    public void requireApprove() {
        if (!canApprove) {
            throw new IllegalArgumentException("Only pending bookings can be approved");
        }
    }

    public void requireDeny() {
        if (!canDeny) {
            throw new IllegalArgumentException("Only pending bookings can be denied");
        }
    }

    public void requireCancel() {
        if (!canCancel) {
            throw new IllegalArgumentException("Only pending or approved bookings can be cancelled");
        }
    }
}
