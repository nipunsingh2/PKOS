package com.pkos.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateNoteRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 5, message = "Content must contain at least 5 characters")
    private String content;

    @Size(max = 20, message = "Color cannot exceed 20 characters")
    private String color;
}