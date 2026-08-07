package com.pkos.backend.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.pkos.backend.config.LlmProperties;
import com.pkos.backend.dto.llm.LLMRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class FallbackLLMService
        implements LLMService {

    private final OpenRouterLLMService openRouterLLMService;

    private final GeminiLLMService geminiLLMService;

    private final LlmProperties llmProperties;

    private final LlmRetryExecutor retryExecutor;

    @Override
    public String generateResponse(
            LLMRequest request
    ) {

        if ("openrouter".equalsIgnoreCase(
                llmProperties.getProvider())) {

            return executeWithFallback(
                    request,
                    openRouterLLMService,
                    geminiLLMService
            );
        }

        return executeWithFallback(
                request,
                geminiLLMService,
                openRouterLLMService
        );
    }

    private String executeWithFallback(
            LLMRequest request,
            LLMService primary,
            LLMService secondary
    ) {

        try {

            return primary.generateResponse(request);

        } catch (RuntimeException exception) {

            if (!llmProperties
                    .getFallback()
                    .isEnabled()
                    || !retryExecutor.isRetryable(exception)) {

                throw exception;
            }

            log.warn(
                    "Primary LLM provider failed. Falling back to secondary provider.",
                    exception
            );

            return secondary.generateResponse(request);
        }
    }

}