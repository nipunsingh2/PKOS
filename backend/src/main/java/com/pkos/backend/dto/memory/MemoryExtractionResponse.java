package com.pkos.backend.dto.memory;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoryExtractionResponse {

    private List<MemoryCandidate> memories;

}