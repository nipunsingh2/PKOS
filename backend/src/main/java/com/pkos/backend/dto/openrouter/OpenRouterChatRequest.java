package com.pkos.backend.dto.openrouter;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenRouterChatRequest {

    private String model;

    private List<OpenRouterMessage> messages;

}