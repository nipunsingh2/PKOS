package com.pkos.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TagResponse {

    private final Long id;

    private final String name;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;
}