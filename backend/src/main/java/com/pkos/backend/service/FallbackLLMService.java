package com.pkos.backend.service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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

    @Override
    public void streamResponse(
            LLMRequest request,
            Consumer<String> chunkConsumer
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "LLM request cannot be null."
            );
        }

        if (chunkConsumer == null) {
            throw new IllegalArgumentException(
                    "Chunk consumer cannot be null."
            );
        }

        if ("openrouter".equalsIgnoreCase(
                llmProperties.getProvider())) {

            executeStreamingWithFallback(
                    request,
                    openRouterLLMService,
                    geminiLLMService,
                    chunkConsumer
            );

            return;
        }

        executeStreamingWithFallback(
                request,
                geminiLLMService,
                openRouterLLMService,
                chunkConsumer
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

    private void executeStreamingWithFallback(
            LLMRequest request,
            LLMService primary,
            LLMService secondary,
            Consumer<String> chunkConsumer
    ) {

        AtomicBoolean chunkEmitted =
                new AtomicBoolean(false);

        Consumer<String> trackingConsumer =
                chunk -> {
                    chunkEmitted.set(true);
                    chunkConsumer.accept(chunk);
                };

        try {

            primary.streamResponse(
                    request,
                    trackingConsumer
            );

        } catch (RuntimeException exception) {

            if (chunkEmitted.get()
                    || !llmProperties
                            .getFallback()
                            .isEnabled()
                    || !retryExecutor
                            .isRetryable(exception)) {

                throw exception;
            }

            log.warn(
                    "Primary streaming provider failed before emitting a response chunk. Falling back to secondary provider.",
                    exception
            );

            secondary.streamResponse(
                    request,
                    chunkConsumer
            );
        }
    }
}