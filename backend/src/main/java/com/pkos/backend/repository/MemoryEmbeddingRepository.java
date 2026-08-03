package com.pkos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.MemoryEmbedding;

public interface MemoryEmbeddingRepository
        extends JpaRepository<MemoryEmbedding, Long> {

    Optional<MemoryEmbedding> findByMemory(
            Memory memory
    );

    boolean existsByMemory(
            Memory memory
    );

}