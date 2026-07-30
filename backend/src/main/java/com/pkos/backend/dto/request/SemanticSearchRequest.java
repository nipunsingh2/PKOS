package com.pkos.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SemanticSearchRequest {

    @NotBlank(message = "Search query is required")
    @Size(max = 1000, message = "Search query cannot exceed 1000 characters")
    private String query;

}