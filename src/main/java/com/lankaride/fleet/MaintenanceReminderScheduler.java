package com.lankaride.fleet;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceReminderScheduler {

    private final MaintenanceService maintenanceService;

    public MaintenanceReminderScheduler(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendMorningReminders() {
        maintenanceService.sendDueReminders();
    }
}
