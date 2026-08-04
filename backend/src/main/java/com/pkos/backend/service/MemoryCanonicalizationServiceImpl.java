package com.pkos.backend.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pkos.backend.dto.llm.LLMRequest;
import com.pkos.backend.dto.memory.MemoryCandidate;
import com.pkos.backend.dto.memory.canonicalization.CanonicalMemoryResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoryCanonicalizationServiceImpl
        implements MemoryCanonicalizationService {

    private final MemoryCanonicalizationPromptService
            promptService;

    private final LLMService
            llmService;

    private final ObjectMapper
            objectMapper;

    @Override
    public MemoryCandidate canonicalize(
            MemoryCandidate memoryCandidate
    ) {

        try {

            String prompt =
                    promptService.buildPrompt(
                            memoryCandidate
                    );

            String response =
                    llmService.generateResponse(
                            LLMRequest.builder()
                                    .prompt(prompt)
                                    .build()
                    );

            CanonicalMemoryResponse canonical =
                    objectMapper.readValue(
                            response,
                            CanonicalMemoryResponse.class
                    );

            return MemoryCandidate.builder()
                    .memoryType(
                            memoryCandidate.getMemoryType()
                    )
                    .value(
                            canonical.getValue()
                    )
                    .build();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to canonicalize memory.",
                    exception
            );
        }
    }

}