package com.pkos.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.entity.Memory;
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

    private final CurrentUserService currentUserService;

    private final MemoryExtractionService
            memoryExtractionService;

    private final MemoryService
            memoryService;

    @Override
    public void processConversation(
            String conversationSummary,
            List<String> userMessages
    ) {

        if (userMessages == null || userMessages.isEmpty()) {
            return;
        }

        User user =
                currentUserService.getCurrentUser();

        List<MemoryCandidate> candidates =
                memoryExtractionService.extractMemories(
                        conversationSummary,
                        userMessages
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
    }

}