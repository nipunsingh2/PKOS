package com.pkos.backend.dto.response;

import java.time.LocalDateTime;
import com.pkos.backend.entity.MessageRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationResponse {

    private Long id;

    private String title;

    private String lastMessage;

    private MessageRole lastMessageRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}