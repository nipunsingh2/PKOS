package com.pkos.backend.service;

import com.pkos.backend.entity.Conversation;

public interface MemoryManager {

    void processConversation(
            Conversation conversation
    );

}