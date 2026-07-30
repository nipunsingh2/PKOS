package com.pkos.backend.service;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.pkos.backend.config.GeminiProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiChatServiceImpl implements GeminiChatService {

    private final Client client;

    private final GeminiProperties geminiProperties;

    @Override
    public String generateResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be null or blank.");
        }

        GenerateContentResponse response =
                client.models.generateContent(
                        geminiProperties.getChatModel(),
                        prompt,
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