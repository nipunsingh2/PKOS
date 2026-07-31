package com.pkos.backend.exception;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(Long conversationId) {
        super("Conversation not found with ID: " + conversationId);
    }
}