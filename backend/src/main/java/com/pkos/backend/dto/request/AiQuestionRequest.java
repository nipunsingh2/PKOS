package com.pkos.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AiQuestionRequest {

    @NotBlank(message = "Question cannot be blank")
    @Size(max = 2000, message = "Question cannot exceed 2000 characters")
    private String question;

}