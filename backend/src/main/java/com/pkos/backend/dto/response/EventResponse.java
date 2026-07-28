package com.pkos.backend.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean allDay;

    private String location;

    private String color;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime remindAt;
}