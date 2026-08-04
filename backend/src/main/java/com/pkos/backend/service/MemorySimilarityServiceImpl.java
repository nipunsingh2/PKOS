package com.pkos.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.User;
import com.pkos.backend.repository.MemoryEmbeddingRepository;
import com.pkos.backend.repository.projection.MemorySimilarityProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemorySimilarityServiceImpl
        implements MemorySimilarityService {

    private final MemoryEmbeddingRepository
            memoryEmbeddingRepository;

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

}