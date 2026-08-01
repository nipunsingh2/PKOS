package com.pkos.backend.dto.memory;

import com.pkos.backend.entity.enums.MemoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoryCandidate {

    private MemoryType memoryType;

    private String key;

    private String value;

}