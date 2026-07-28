package com.pkos.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.dto.request.CreateEventRequest;
import com.pkos.backend.dto.request.UpdateEventRequest;
import com.pkos.backend.dto.response.EventResponse;
import com.pkos.backend.entity.Event;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.InvalidEventTimeException;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.EventMapper;
import com.pkos.backend.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CurrentUserService currentUserService;

    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        validateEventTime(
                request.getStartTime(),
                request.getEndTime());

        validateReminderTime(
                request.getRemindAt(),
                request.getStartTime());

        User currentUser = currentUserService.getCurrentUser();

        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setAllDay(request.isAllDay());
        event.setLocation(request.getLocation());
        event.setColor(request.getColor());
        event.setRemindAt(request.getRemindAt());
        event.setUser(currentUser);

        eventRepository.save(event);

        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse updateEvent(
            Long eventId,
            UpdateEventRequest request) {

        validateEventTime(
                request.getStartTime(),
                request.getEndTime());

        validateReminderTime(
                request.getRemindAt(),
                request.getStartTime());

        User currentUser = currentUserService.getCurrentUser();

        Event event = findOwnedEvent(eventId, currentUser);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setAllDay(request.isAllDay());
        event.setLocation(request.getLocation());
        event.setColor(request.getColor());
        event.setRemindAt(request.getRemindAt());

        return eventMapper.toResponse(event);
    }

    @Override
    public void deleteEvent(Long eventId) {

        User currentUser = currentUserService.getCurrentUser();

        Event event = findOwnedEvent(eventId, currentUser);

        eventRepository.delete(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long eventId) {

        User currentUser = currentUserService.getCurrentUser();

        Event event = findOwnedEvent(eventId, currentUser);

        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {

        User currentUser = currentUserService.getCurrentUser();

        return eventRepository
                .findByUserOrderByStartTimeAsc(currentUser)
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        User currentUser = currentUserService.getCurrentUser();

        return eventRepository
                .findByUserAndStartTimeBetweenOrderByStartTimeAsc(
                        currentUser,
                        start,
                        end)
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    private Event findOwnedEvent(Long eventId, User user) {

        return eventRepository
                .findByIdAndUser(eventId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Event not found"));
    }

    private void validateEventTime(
            LocalDateTime start,
            LocalDateTime end) {

        if (!start.isBefore(end)) {
            throw new InvalidEventTimeException(
                    "Event start time must be before end time");
        }
    }

    private void validateReminderTime(
            LocalDateTime remindAt,
            LocalDateTime startTime) {

        if (remindAt != null && !remindAt.isBefore(startTime)) {
            throw new InvalidEventTimeException(
                    "Reminder time must be before event start time");
        }
    }
}