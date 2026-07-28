package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.EventResponse;
import com.pkos.backend.entity.Event;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event) {

        EventResponse response = new EventResponse();

        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setAllDay(event.isAllDay());
        response.setLocation(event.getLocation());
        response.setColor(event.getColor());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        response.setRemindAt(event.getRemindAt());

        return response;
    }
}