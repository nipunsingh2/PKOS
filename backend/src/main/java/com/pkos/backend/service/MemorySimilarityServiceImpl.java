package com.pkos.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.config.MemoryProperties;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.User;
import com.pkos.backend.repository.MemoryEmbeddingRepository;
import com.pkos.backend.repository.MemoryRepository;
import com.pkos.backend.repository.projection.MemorySimilarityProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemorySimilarityServiceImpl
        implements MemorySimilarityService {

    private final MemoryEmbeddingRepository
            memoryEmbeddingRepository;

    private final MemoryRepository
            memoryRepository;

    private final MemoryProperties
            memoryProperties;

    @Override
    public Optional<MemorySimilarityProjection> findMostSimilar(
            User user,
            float[] embedding
    ) {

        return memoryEmbeddingRepository.findMostSimilar(
                user.getId(),
                embedding
        );
    }

    @Override
    public List<Memory> findTopRelevant(
            User user,
            float[] embedding
    ) {

        List<MemorySimilarityProjection> projections =
                memoryEmbeddingRepository.findTopRelevant(
                        user.getId(),
                        embedding,
                        memoryProperties.getSemanticTopK()
                );

        return projections.stream()
                .map(MemorySimilarityProjection::getMemoryId)
                .map(id ->
                        memoryRepository.findByIdAndUser(id, user)
                                .orElse(null)
                )
                .filter(memory -> memory != null)
                .toList();
    }

}