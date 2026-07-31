package com.pkos.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateConversationRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

}