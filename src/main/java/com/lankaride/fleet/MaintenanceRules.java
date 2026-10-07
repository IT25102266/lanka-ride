package com.lankaride.fleet;

import com.lankaride.common.MaintenanceStatus;

/**
 * Singleton. The maintenance rules never change and the application needs
 * only one copy of them. The constructor is private so callers share
 * {@link #getInstance()} instead of creating their own.
 */
public final class MaintenanceRules {

    private static final MaintenanceRules INSTANCE = new MaintenanceRules();

    private MaintenanceRules() {
    }

    public static MaintenanceRules getInstance() {
        return INSTANCE;
    }

    public void requireEditable(MaintenanceStatus status) {
        if (status == MaintenanceStatus.CLOSED) {
            throw new IllegalArgumentException("Closed records cannot be edited");
        }
    }

    public boolean isClosed(MaintenanceStatus status) {
        return status == MaintenanceStatus.CLOSED;
    }

    /**
     * An edit form cannot close a job. Closing goes through {@code close},
     * so a CLOSED choice on the edit form stays IN_PROGRESS.
     */
    public MaintenanceStatus statusAfterEdit(MaintenanceStatus requested) {
        if (requested == MaintenanceStatus.CLOSED) {
            return MaintenanceStatus.IN_PROGRESS;
        }
        return requested;
    }
}
