package com.pkos.backend.service;

import com.pkos.backend.dto.memory.MemoryCandidate;

public interface MemoryCanonicalizationPromptService {

    String buildPrompt(
            MemoryCandidate memoryCandidate
    );

}