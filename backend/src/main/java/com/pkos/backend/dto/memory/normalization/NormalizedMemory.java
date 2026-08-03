package com.pkos.backend.dto.memory.normalization;

import com.pkos.backend.entity.enums.MemoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a normalized memory produced by the Memory Normalization
 * pipeline before it is validated and persisted.
 *
 * <p>
 * This is a pipeline DTO and is intentionally independent of JPA.
 * It carries only the information required for downstream memory
 * processing.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalizedMemory {

    /**
     * Memory category determined during extraction.
     */
    private MemoryType memoryType;

    /**
     * Original memory text produced by the extraction step.
     */
    private String originalValue;

    /**
     * Canonical representation of the memory.
     */
    private String normalizedValue;

}