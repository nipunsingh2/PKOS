package com.pkos.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import com.pkos.backend.dto.request.CreateEventRequest;
import com.pkos.backend.dto.request.UpdateEventRequest;
import com.pkos.backend.dto.response.EventResponse;

public interface EventService {

    EventResponse createEvent(CreateEventRequest request);

    EventResponse updateEvent(Long eventId, UpdateEventRequest request);

    void deleteEvent(Long eventId);

    EventResponse getEvent(Long eventId);

    List<EventResponse> getAllEvents();

    List<EventResponse> getEventsBetween(
            LocalDateTime start,
            LocalDateTime end);
}