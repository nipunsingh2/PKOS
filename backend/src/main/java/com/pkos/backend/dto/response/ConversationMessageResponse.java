package com.pkos.backend.dto.response;

import java.time.LocalDateTime;

import com.pkos.backend.entity.MessageRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationMessageResponse {

    private Long id;

    private MessageRole role;

    private String content;

    private LocalDateTime createdAt;

}