package com.pkos.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReminderResponse {

    private Long id;

    private Long noteId;

    private LocalDateTime remindAt;

    private boolean completed;

    private LocalDateTime createdAt;
}