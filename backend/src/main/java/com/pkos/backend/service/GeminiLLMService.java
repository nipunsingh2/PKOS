package com.pkos.backend.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.pkos.backend.config.GeminiProperties;
import com.pkos.backend.dto.llm.LLMRequest;

import lombok.RequiredArgsConstructor;

@Service
@ConditionalOnProperty(
        prefix = "llm",
        name = "provider",
        havingValue = "gemini"
)
@RequiredArgsConstructor
public class GeminiLLMService
        implements LLMService {

    private final Client client;

    private final GeminiProperties geminiProperties;

    @Override
    public String generateResponse(
            LLMRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "LLM request cannot be null."
            );
        }

        if (request.getPrompt() == null
                || request.getPrompt().isBlank()) {

            throw new IllegalArgumentException(
                    "Prompt cannot be null or blank."
            );
        }

        GenerateContentResponse response =
                client.models.generateContent(
                        geminiProperties.getChatModel(),
                        request.getPrompt(),
                        null
                );

        String text = response.text();

        if (text == null || text.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        return text;
    }

}