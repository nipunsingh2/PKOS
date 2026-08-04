package com.pkos.backend.service;

import java.util.Optional;

import com.pkos.backend.entity.User;
import com.pkos.backend.repository.projection.MemorySimilarityProjection;

public interface MemorySimilarityService {

    Optional<MemorySimilarityProjection> findMostSimilar(
            User user,
            float[] embedding
    );

}