package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.dto.memory.MemoryCandidate;

public interface MemoryExtractionService {

    List<MemoryCandidate> extractMemories(
            String conversationSummary,
            List<String> userMessages
    );

}