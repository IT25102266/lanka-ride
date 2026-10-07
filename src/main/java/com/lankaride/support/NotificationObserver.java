package com.lankaride.support;

/**
 * Observer. {@link NotificationService} saves the log row and then tells
 * every observer. Adding a new reaction does not require editing the save.
 */
public interface NotificationObserver {

    void onNotified(NotificationLog entry);
}
