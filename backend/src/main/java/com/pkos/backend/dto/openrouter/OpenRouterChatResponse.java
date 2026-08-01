package com.pkos.backend.dto.openrouter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenRouterChatResponse {

    private List<OpenRouterChoice> choices;

}