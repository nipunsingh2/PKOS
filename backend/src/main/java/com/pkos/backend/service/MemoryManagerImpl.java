package com.pkos.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.ConversationSummary;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.MessageRole;
import com.pkos.backend.entity.User;
import com.pkos.backend.entity.enums.MemorySource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoryManagerImpl
        implements MemoryManager {

    private static final BigDecimal DEFAULT_CONFIDENCE =
            BigDecimal.valueOf(0.90);

    private final ConversationService conversationService;

    private final ConversationSummaryService
            conversationSummaryService;

    private final MemoryExtractionService
            memoryExtractionService;

    private final MemoryService
            memoryService;

    @Override
    @Async
    public void processConversation(
            Conversation conversation
    ) {

        User user = conversation.getUser();

        ConversationSummary conversationSummary =
                conversationSummaryService
                        .getSummary(conversation)
                        .orElse(null);

        String summary = null;

        int processedUserMessageCount = 0;

        if (conversationSummary != null) {
            summary = conversationSummary.getSummary();
            processedUserMessageCount =
                    conversationSummary
                            .getMemoryProcessedMessageCount();
        }

        List<ConversationMessage> allMessages =
                conversationService.getConversationHistory(
                        conversation
                );

        List<ConversationMessage> userMessages =
                allMessages.stream()
                        .filter(message ->
                                message.getRole() == MessageRole.USER
                        )
                        .toList();

        if (processedUserMessageCount >=
                userMessages.size()) {
            return;
        }

        List<String> newUserMessages =
                userMessages.stream()
                        .skip(processedUserMessageCount)
                        .map(ConversationMessage::getContent)
                        .toList();

        if (newUserMessages.isEmpty()) {
            return;
        }

        List<MemoryCandidate> candidates =
                memoryExtractionService.extractMemories(
                        summary,
                        newUserMessages
                );

        for (MemoryCandidate candidate : candidates) {

            if (candidate == null) {
                continue;
            }

            if (candidate.getMemoryType() == null) {
                continue;
            }

            if (candidate.getValue() == null ||
                    candidate.getValue().isBlank()) {
                continue;
            }

            String value =
                    candidate.getValue().trim();

            if (memoryService.exists(
                    user,
                    candidate.getMemoryType(),
                    value
            )) {
                continue;
            }

            Memory memory =
                    Memory.builder()
                            .user(user)
                            .memoryType(
                                    candidate.getMemoryType()
                            )
                            .value(value)
                            .confidence(DEFAULT_CONFIDENCE)
                            .source(MemorySource.AI_CHAT)
                            .build();

            memoryService.save(memory);
        }

        if (conversationSummary != null) {

            conversationSummaryService
                    .updateMemoryProcessedCount(
                            conversation,
                            userMessages.size()
                    );
        }
    }

}