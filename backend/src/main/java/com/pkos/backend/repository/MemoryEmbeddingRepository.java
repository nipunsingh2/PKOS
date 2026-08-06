package com.pkos.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.MemoryEmbedding;
import com.pkos.backend.repository.projection.MemorySimilarityProjection;

public interface MemoryEmbeddingRepository
        extends JpaRepository<MemoryEmbedding, Long> {

    Optional<MemoryEmbedding> findByMemory(
            Memory memory
    );

    boolean existsByMemory(
            Memory memory
    );

    @Query(
            value = """
                    SELECT
                    m.id AS memoryId,
                    m.memory_type AS memoryType,
                    m.value AS value,
                    m.normalized_value AS normalizedValue,
                    1 - (me.embedding <=> CAST(:embedding AS vector))
                            AS similarity
                    FROM memory_embeddings me
                    JOIN memories m
                    ON me.memory_id = m.id
                    WHERE m.user_id = :userId
                    ORDER BY me.embedding <=> CAST(:embedding AS vector)
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<MemorySimilarityProjection> findMostSimilar(
            @Param("userId") Long userId,
            @Param("embedding") float[] embedding
    );

    @Query(
            value = """
                    SELECT
                    m.id AS memoryId,
                    m.memory_type AS memoryType,
                    m.value AS value,
                    m.normalized_value AS normalizedValue,
                    1 - (me.embedding <=> CAST(:embedding AS vector))
                            AS similarity
                    FROM memory_embeddings me
                    JOIN memories m
                    ON me.memory_id = m.id
                    WHERE m.user_id = :userId
                    ORDER BY me.embedding <=> CAST(:embedding AS vector)
                    LIMIT :limit
                    """,
            nativeQuery = true
    )
    List<MemorySimilarityProjection> findTopRelevant(
            @Param("userId") Long userId,
            @Param("embedding") float[] embedding,
            @Param("limit") int limit
    );

}