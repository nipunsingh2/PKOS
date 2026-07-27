package com.pkos.backend.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.Reminder;
import com.pkos.backend.repository.ReminderRepository;
import com.pkos.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(ReminderScheduler.class);

    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void checkDueReminders() {

        List<Reminder> dueReminders =
                reminderRepository.findByCompletedFalseAndNotifiedFalseAndRemindAtLessThanEqual(
                        LocalDateTime.now());

        if (dueReminders.isEmpty()) {
            return;
        }

        log.info("Found {} due reminder(s).", dueReminders.size());

        for (Reminder reminder : dueReminders) {

            log.info("""
                    
                    ===============================
                    REMINDER DUE
                    User ID : {}
                    Note ID : {}
                    Reminder: {}
                    ===============================
                    """,
                    reminder.getNote().getUser().getId(),
                    reminder.getNote().getId(),
                    reminder.getRemindAt());

            notificationService.createReminderNotification(reminder);

            reminder.setNotified(true);
            reminderRepository.save(reminder);
        }
    }
}