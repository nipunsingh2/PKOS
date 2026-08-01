package com.pkos.backend.service;

import java.util.List;

public interface MemoryManager {

    void processConversation(
            String conversationSummary,
            List<String> userMessages
    );

}