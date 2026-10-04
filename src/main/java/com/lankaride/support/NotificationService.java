package com.lankaride.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    @Transactional
    public void send(String channel, String recipient, String subject, String body) {
        NotificationLog entry = new NotificationLog();
        entry.setChannel(channel);
        entry.setRecipient(recipient);
        entry.setSubject(subject);
        entry.setBody(body);
        boolean deliverable = recipient != null && !recipient.isBlank();
        entry.setDeliveryStatus(deliverable ? "SENT" : "FAILED");
        notificationLogRepository.save(entry);
        log.info("NOTIFY [{}] status={} to={} | {} | {}",
                channel, entry.getDeliveryStatus(), recipient, subject, body);
    }

    @Transactional
    public int retryFailed() {
        List<NotificationLog> failed = notificationLogRepository.findByDeliveryStatusOrderByCreatedAtAsc("FAILED");
        int retried = 0;
        for (NotificationLog entry : failed) {
            if (entry.getRecipient() == null || entry.getRecipient().isBlank()) {
                continue;
            }
            entry.setDeliveryStatus("SENT");
            entry.setRetryCount(entry.getRetryCount() + 1);
            notificationLogRepository.save(entry);
            log.info("RETRY [{}] to={} | {}", entry.getChannel(), entry.getRecipient(), entry.getSubject());
            retried++;
        }
        return retried;
    }

    @Transactional
    public void email(String recipient, String subject, String body) {
        send("EMAIL", recipient, subject, body);
    }
}
