package com.lankaride.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * One observer: write the same delivery line the service used to log itself.
 */
@Component
public class LoggingNotificationObserver implements NotificationObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationObserver.class);

    @Override
    public void onNotified(NotificationLog entry) {
        log.info("NOTIFY [{}] status={} to={} | {} | {}",
                entry.getChannel(), entry.getDeliveryStatus(), entry.getRecipient(),
                entry.getSubject(), entry.getBody());
    }
}
