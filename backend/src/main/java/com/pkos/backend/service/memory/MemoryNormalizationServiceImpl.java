package com.pkos.backend.service.memory;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.dto.memory.normalization.NormalizedMemory;

@Service
public class MemoryNormalizationServiceImpl
        implements MemoryNormalizationService {

    @Override
    public NormalizedMemory normalize(
            MemoryCandidate memoryCandidate
    ) {

        Objects.requireNonNull(
                memoryCandidate,
                "memoryCandidate must not be null"
        );

        String originalValue = memoryCandidate.getValue();
        String normalizedValue = originalValue;

        normalizedValue = normalizeWhitespace(normalizedValue);
        normalizedValue = normalizeCapitalization(normalizedValue);
        normalizedValue = normalizeSentence(normalizedValue);

        return NormalizedMemory.builder()
                .memoryType(memoryCandidate.getMemoryType())
                .originalValue(originalValue)
                .normalizedValue(normalizedValue)
                .build();
    }

    private String normalizeWhitespace(
            String value
    ) {

        return value.trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizeCapitalization(
            String value
    ) {

        if (value.isBlank()) {
            return value;
        }

        return Character.toUpperCase(value.charAt(0))
                + value.substring(1);
    }

    private String normalizeSentence(
            String value
    ) {

        if (value.endsWith(".")) {
            return value;
        }

        return value + ".";
    }

}