package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.ConversationMessageResponse;
import com.pkos.backend.entity.ConversationMessage;

@Component
public class ConversationMessageMapper {

    public ConversationMessageResponse toResponse(
            ConversationMessage message
    ) {

        if (message == null) {
            return null;
        }

        return ConversationMessageResponse.builder()
                .id(message.getId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

}