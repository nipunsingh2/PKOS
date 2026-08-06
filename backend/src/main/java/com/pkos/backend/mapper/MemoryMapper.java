package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.MemoryResponse;
import com.pkos.backend.entity.Memory;

@Component
public class MemoryMapper {

    public MemoryResponse toResponse(
            Memory memory) {

        return MemoryResponse.builder()
                .id(memory.getId())
                .memoryType(memory.getMemoryType())
                .value(memory.getValue())
                .confidence(memory.getConfidence())
                .observationCount(memory.getObservationCount())
                .status(memory.getStatus())
                .source(memory.getSource())
                .createdAt(memory.getCreatedAt())
                .updatedAt(memory.getUpdatedAt())
                .build();
    }
}