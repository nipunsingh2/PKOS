package com.pkos.backend.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.Event;
import com.pkos.backend.repository.EventRepository;
import com.pkos.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventReminderScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(EventReminderScheduler.class);

    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void checkDueEventReminders() {

        List<Event> dueEvents =
                eventRepository.findByReminderSentFalseAndRemindAtLessThanEqual(
                        LocalDateTime.now());

        if (dueEvents.isEmpty()) {
            return;
        }

        log.info("Found {} due event reminder(s).", dueEvents.size());

        for (Event event : dueEvents) {

            log.info("""
                    
                    ===============================
                    EVENT REMINDER DUE
                    User ID : {}
                    Event ID: {}
                    Event   : {}
                    Reminder: {}
                    ===============================
                    """,
                    event.getUser().getId(),
                    event.getId(),
                    event.getTitle(),
                    event.getRemindAt());

            notificationService.createEventReminderNotification(event);

            event.setReminderSent(true);
            eventRepository.save(event);
        }
    }
}