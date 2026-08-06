package com.pkos.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pkos.backend.entity.enums.MemorySource;
import com.pkos.backend.entity.enums.MemoryStatus;
import com.pkos.backend.entity.enums.MemoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryResponse {

    private Long id;

    private MemoryType memoryType;

    private String value;

    private BigDecimal confidence;

    private Integer observationCount;

    private MemoryStatus status;

    private MemorySource source;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}