package com.pkos.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.config.GeminiProperties;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.MemoryEmbedding;
import com.pkos.backend.repository.MemoryEmbeddingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoryEmbeddingServiceImpl
        implements MemoryEmbeddingService {

    private final MemoryEmbeddingRepository
            memoryEmbeddingRepository;

    private final GeminiProperties
            geminiProperties;

        @Override
        public MemoryEmbedding create(
                Memory memory,
                float[] embedding
        ) {

        Optional<MemoryEmbedding> existing =
                memoryEmbeddingRepository.findByMemory(
                        memory
                );

        if (existing.isPresent()) {
                return existing.get();
        }

        MemoryEmbedding memoryEmbedding =
                MemoryEmbedding.builder()
                        .memory(memory)
                        .embedding(embedding)
                        .embeddingModel(
                                geminiProperties.getEmbeddingModel()
                        )
                        .build();

        return memoryEmbeddingRepository.save(
                memoryEmbedding
        );
        }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemoryEmbedding> getByMemory(
            Memory memory
    ) {

        return memoryEmbeddingRepository.findByMemory(
                memory
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(
            Memory memory
    ) {

        return memoryEmbeddingRepository.existsByMemory(
                memory
        );
    }

}  