package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.ConversationResponse;
import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;

@Component
public class ConversationMapper {

    public ConversationResponse toResponse(
            Conversation conversation
    ) {

        if (conversation == null) {
            return null;
        }

        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }


    public ConversationResponse toResponse(
            Conversation conversation,
            ConversationMessage lastMessage
    ) {

        if (conversation == null) {
            return null;
        }

        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .lastMessage(
                        lastMessage == null
                                ? null
                                : lastMessage.getContent()
                )
                .lastMessageRole(
                        lastMessage == null
                                ? null
                                : lastMessage.getRole()
                )
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

}