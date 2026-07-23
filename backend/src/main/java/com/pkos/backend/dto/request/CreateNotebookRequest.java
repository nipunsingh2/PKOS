package com.pkos.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateNotebookRequest {

    @NotBlank(message = "Notebook name is required")
    @Size(max = 100, message = "Notebook name cannot exceed 100 characters")
    private String name;
}