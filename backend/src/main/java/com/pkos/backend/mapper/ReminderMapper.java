package com.pkos.backend.mapper;

import com.pkos.backend.dto.response.ReminderResponse;
import com.pkos.backend.entity.Reminder;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    public ReminderResponse toResponse(Reminder reminder) {

        ReminderResponse response = new ReminderResponse();

        response.setId(reminder.getId());
        response.setNoteId(reminder.getNote().getId());
        response.setRemindAt(reminder.getRemindAt());
        response.setCompleted(reminder.isCompleted());
        response.setCreatedAt(reminder.getCreatedAt());

        return response;
    }
}