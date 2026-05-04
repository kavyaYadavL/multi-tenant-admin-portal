package com.internship.tool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final EmailService emailService;

    // Runs every day at 8:00 AM server time
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReminder() {
        log.info("Starting daily reminder job...");
        
        // Example: Send an email to the admin
        String adminEmail = "admin@example.com";
        String subject = "Daily Admin Portal Summary";
        String text = "Hello Admin, this is your daily automated reminder from the Multi-Tenant Portal.";
        
        emailService.sendEmail(adminEmail, subject, text);
        
        log.info("Finished daily reminder job.");
    }
}
