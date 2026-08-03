package com.pkos.backend.service;

import java.util.Optional;

import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.MemoryEmbedding;

public interface MemoryEmbeddingService {

    MemoryEmbedding create(
            Memory memory
    );

    Optional<MemoryEmbedding> getByMemory(
            Memory memory
    );

    boolean exists(
            Memory memory
    );

}