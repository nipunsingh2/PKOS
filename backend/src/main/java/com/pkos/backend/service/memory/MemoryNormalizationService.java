package com.pkos.backend.service.memory;

import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.dto.memory.normalization.NormalizedMemory;

public interface MemoryNormalizationService {

    NormalizedMemory normalize(
            MemoryCandidate memoryCandidate
    );

}