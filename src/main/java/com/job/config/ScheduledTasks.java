package com.job.config;

import com.job.service.PremiumSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final PremiumSubscriptionService premiumSubscriptionService;

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void checkExpiredSubscriptions() {
        try {
            log.info("Starting scheduled check for expired subscriptions");
            premiumSubscriptionService.checkAndExpireSubscriptions();
            log.info("Completed scheduled check for expired subscriptions");
        } catch (Exception e) {
            log.error("Error during scheduled check for expired subscriptions: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 3 * * SUN") // Run weekly on Sunday at 3 AM
    public void weeklyMaintenance() {
        try {
            log.info("Starting weekly maintenance tasks");
            // Add any other weekly maintenance tasks here
            log.info("Completed weekly maintenance tasks");
        } catch (Exception e) {
            log.error("Error during weekly maintenance: {}", e.getMessage(), e);
        }
    }
}
