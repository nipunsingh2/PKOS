package com.pkos.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTagRequest {

    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name cannot exceed 50 characters")
    private String name;
}