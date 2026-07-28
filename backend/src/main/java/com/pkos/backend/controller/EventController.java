package com.pkos.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pkos.backend.dto.request.CreateEventRequest;
import com.pkos.backend.dto.request.UpdateEventRequest;
import com.pkos.backend.dto.response.EventResponse;
import com.pkos.backend.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(
            @Valid @RequestBody CreateEventRequest request) {

        return eventService.createEvent(request);
    }

    @PutMapping("/{eventId}")
    public EventResponse updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest request) {

        return eventService.updateEvent(eventId, request);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @PathVariable Long eventId) {

        eventService.deleteEvent(eventId);
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(
            @PathVariable Long eventId) {

        return eventService.getEvent(eventId);
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {

        return eventService.getAllEvents();
    }

    @GetMapping("/range")
    public List<EventResponse> getEventsBetween(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        return eventService.getEventsBetween(start, end);
    }
}