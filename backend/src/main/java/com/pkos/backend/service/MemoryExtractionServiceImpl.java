package com.pkos.backend.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.dto.memory.MemoryExtractionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryExtractionServiceImpl
        implements MemoryExtractionService {

    private final MemoryExtractionPromptService
            memoryExtractionPromptService;

    private final GeminiChatService
            geminiChatService;

    private final ObjectMapper objectMapper;

    @Override
    public List<MemoryCandidate> extractMemories(
            String conversationSummary,
            List<String> userMessages
    ) {

        if (userMessages == null || userMessages.isEmpty()) {
            return Collections.emptyList();
        }

        String prompt =
                memoryExtractionPromptService.buildPrompt(
                        conversationSummary,
                        userMessages
                );

        String response =
                geminiChatService.generateResponse(prompt);

        try {

            ObjectMapper mapper =
                    objectMapper.copy();

            mapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false
            );

            MemoryExtractionResponse extractionResponse =
                    mapper.readValue(
                            response,
                            MemoryExtractionResponse.class
                    );

            if (extractionResponse == null ||
                    extractionResponse.getMemories() == null) {

                return Collections.emptyList();
            }

            return extractionResponse.getMemories();

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Failed to parse Gemini memory extraction response.",
                    exception
            );
        }
    }

}