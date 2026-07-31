package com.pkos.backend.service;

import com.pkos.backend.entity.Conversation;

public interface ConversationSummaryManager {

    void updateSummaryIfRequired(
            Conversation conversation
    );

}