package com.pkos.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.pkos.backend.config.MemoryProperties;
import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.dto.memory.normalization.NormalizedMemory;
import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.ConversationSummary;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.MessageRole;
import com.pkos.backend.entity.User;
import com.pkos.backend.entity.enums.MemorySource;
import com.pkos.backend.entity.enums.MemoryStatus;
import com.pkos.backend.repository.projection.MemorySimilarityProjection;
import com.pkos.backend.service.memory.MemoryNormalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    private final MemoryCanonicalizationService
    memoryCanonicalizationService;

    private final MemoryNormalizationService
            memoryNormalizationService;

    private final MemoryService
            memoryService;

    private final MemoryEmbeddingService
            memoryEmbeddingService;

        private final MemorySimilarityService
                memorySimilarityService;

        private final MemoryProperties
                memoryProperties;
        private final TextEmbeddingService
                textEmbeddingService;

    @Override
    @Async
    public void processConversation(
            Conversation conversation
    ) {


        User user = conversation.getUser();

        log.info("Memory manager started for conversation {}", conversation.getId());

        ConversationSummary conversationSummary =
                conversationSummaryService
                        .getSummary(conversation)
                        .orElse(null);

        log.debug("Conversation summary exists: {}", conversationSummary != null);

        String summary = null;

        int processedUserMessageCount = 0;

        log.info("Processed count before processing = {}", processedUserMessageCount);

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

        log.debug("Total user messages: {}", userMessages.size());


        if (processedUserMessageCount >= userMessages.size()) {
            return;
        }

        List<String> newUserMessages =
                userMessages.stream()
                        .skip(processedUserMessageCount)
                        .map(ConversationMessage::getContent)
                        .toList();

        log.debug("New user messages to process: {}", newUserMessages.size());


        if (newUserMessages.isEmpty()) {
            return;
        }

        log.debug("Starting memory extraction");


        List<MemoryCandidate> candidates =
                memoryExtractionService.extractMemories(
                        summary,
                        newUserMessages
                );
        for (MemoryCandidate candidate : candidates) {
        log.debug(
                "Memory candidate -> {} : {}",
                candidate.getMemoryType(),
                candidate.getValue()
        );
        }

        for (MemoryCandidate candidate : candidates) {

        if (!isValidCandidate(candidate)) {
                continue;
        }

        try {

                MemoryCandidate canonicalMemory =
                        memoryCanonicalizationService.canonicalize(
                                candidate
                        );

                NormalizedMemory normalizedMemory =
                        memoryNormalizationService.normalize(
                                canonicalMemory
                        );

                Optional<Memory> existingExactMemory =
                        memoryService.getMemory(
                                user,
                                normalizedMemory.getMemoryType(),
                                normalizedMemory.getNormalizedValue()
                        );

                if (existingExactMemory.isPresent()) {

                memoryService.reinforce(
                        existingExactMemory.get()
                );

                continue;
                }

                float[] embedding =
                        textEmbeddingService.generateEmbedding(
                                normalizedMemory.getNormalizedValue()
                        );

                Memory newMemory =
                        Memory.builder()
                                .user(user)
                                .memoryType(normalizedMemory.getMemoryType())
                                .value(normalizedMemory.getOriginalValue())
                                .normalizedValue(normalizedMemory.getNormalizedValue())
                                .confidence(DEFAULT_CONFIDENCE)
                                .source(MemorySource.AI_CHAT)
                                .observationCount(1)
                                .status(MemoryStatus.CURRENT)
                                .build();

                Optional<MemorySimilarityProjection> similarMemory =
                        memorySimilarityService.findMostSimilar(
                                user,
                                embedding
                        );

                if (similarMemory.isPresent()
                        && similarMemory.get().getSimilarity()
                                >= memoryProperties.getSimilarityThreshold()) {

                MemorySimilarityProjection projection =
                        similarMemory.get();

                Optional<Memory> existingSemanticMemory =
                        memoryService.getMemory(
                                user,
                                projection.getMemoryType(),
                                projection.getNormalizedValue()
                        );

                if (existingSemanticMemory.isPresent()) {

                        memoryService.reinforce(
                                existingSemanticMemory.get()
                        );

                        continue;
                }
                }

                Memory savedMemory =
                        memoryService.save(
                                newMemory
                        );

                memoryEmbeddingService.create(
                        savedMemory,
                        embedding
                );

        } catch (Exception exception) {

                log.error(
                        "Failed to process memory candidate [{} : {}]. Continuing with remaining candidates.",
                        candidate.getMemoryType(),
                        candidate.getValue(),
                        exception
                );
        }
        }

        if (conversationSummary != null) {
            conversationSummaryService
                    .updateMemoryProcessedCount(
                            conversation,
                            userMessages.size()
                    );
        }

    }

    private boolean isValidCandidate(
            MemoryCandidate candidate
    ) {

        return candidate != null
                && candidate.getMemoryType() != null
                && candidate.getValue() != null
                && !candidate.getValue().isBlank();
    }

}