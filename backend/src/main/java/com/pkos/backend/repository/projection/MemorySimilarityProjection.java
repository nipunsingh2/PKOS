package com.pkos.backend.repository.projection;

import com.pkos.backend.entity.enums.MemoryType;

public interface MemorySimilarityProjection {

    Long getMemoryId();

    MemoryType getMemoryType();

    String getValue();

    String getNormalizedValue();

    Double getSimilarity();

}