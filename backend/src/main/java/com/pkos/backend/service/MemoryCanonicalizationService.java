package com.pkos.backend.service;

import com.pkos.backend.dto.memory.MemoryCandidate;

public interface MemoryCanonicalizationService {

    MemoryCandidate canonicalize(
            MemoryCandidate memoryCandidate
    );

}