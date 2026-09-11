package com.lankaride.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        notificationLogRepository.save(entry);
        log.info("NOTIFY [{}] to={} | {} | {}", channel, recipient, subject, body);
    }

    @Transactional
    public void email(String recipient, String subject, String body) {
        send("EMAIL", recipient, subject, body);
    }
}
