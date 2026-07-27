package com.pkos.backend.dto.response;

import java.time.LocalDateTime;

import com.pkos.backend.entity.NotificationType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {

    private Long id;

    private String message;

    private NotificationType type;

    private boolean read;

    private LocalDateTime createdAt;

    private Long reminderId;
}