package com.pkos.backend.service;

import java.util.Optional;

import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationSummary;

public interface ConversationSummaryService {

    Optional<ConversationSummary> getSummary(
            Conversation conversation
    );

    ConversationSummary saveSummary(
            Conversation conversation,
            String summary,
            int summarizedMessageCount
    );

}