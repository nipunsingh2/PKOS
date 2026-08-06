package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.Note;
// import com.pkos.backend.repository.projection.SemanticSearchProjection;

public interface PromptBuilderService {

        String buildPrompt(
                String question,
                List<Note> notes
        );

        String buildConversationPrompt(
                String conversationSummary,
                List<Memory> memories,
                List<Note> notes,
                List<ConversationMessage> conversationHistory,
                String userQuestion
        );

}