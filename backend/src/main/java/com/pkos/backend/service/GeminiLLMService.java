package com.pkos.backend.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.pkos.backend.config.GeminiProperties;
import com.pkos.backend.dto.llm.LLMRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

    private final LlmRetryExecutor retryExecutor;

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

        log.info(
                "Sending request to Gemini using model: {}",
                geminiProperties.getChatModel()
        );

        GenerateContentResponse response =
                retryExecutor.execute(
                        () ->
                                client.models.generateContent(
                                        geminiProperties.getChatModel(),
                                        request.getPrompt(),
                                        null
                                )
                );

        String text = response.text();

        if (text == null || text.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        log.info("Gemini response received successfully.");

        return text;
    }

}