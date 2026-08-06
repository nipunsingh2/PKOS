package com.pkos.backend.service;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.pkos.backend.config.OpenRouterProperties;
import com.pkos.backend.dto.llm.LLMRequest;
import com.pkos.backend.dto.openrouter.OpenRouterChatRequest;
import com.pkos.backend.dto.openrouter.OpenRouterChatResponse;
import com.pkos.backend.dto.openrouter.OpenRouterChoice;
import com.pkos.backend.dto.openrouter.OpenRouterMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(
        prefix = "llm",
        name = "provider",
        havingValue = "openrouter"
)
@RequiredArgsConstructor
public class OpenRouterLLMService implements LLMService {

    private final OpenRouterProperties openRouterProperties;

    private final LlmRetryExecutor retryExecutor;

    private final RestClient restClient =
            RestClient.builder().build();

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
                "Sending request to OpenRouter using model: {}",
                openRouterProperties.getChatModel()
        );

        OpenRouterChatRequest chatRequest =
                OpenRouterChatRequest.builder()
                        .model(openRouterProperties.getChatModel())
                        .messages(
                                List.of(
                                        OpenRouterMessage.builder()
                                                .role("user")
                                                .content(request.getPrompt())
                                                .build()
                                )
                        )
                        .build();

        try {

            OpenRouterChatResponse response =
                    retryExecutor.execute(
                            () ->
                                    restClient.post()
                                            .uri(
                                                    openRouterProperties.getBaseUrl()
                                                            + "/chat/completions"
                                            )
                                            .header(
                                                    HttpHeaders.AUTHORIZATION,
                                                    "Bearer "
                                                            + openRouterProperties.getApiKey()
                                            )
                                            .header(
                                                    "HTTP-Referer",
                                                    openRouterProperties.getSiteUrl()
                                            )
                                            .header(
                                                    "X-OpenRouter-Title",
                                                    openRouterProperties.getAppName()
                                            )
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(chatRequest)
                                            .retrieve()
                                            .body(OpenRouterChatResponse.class)
                    );

            if (response == null
                    || response.getChoices() == null
                    || response.getChoices().isEmpty()) {

                throw new IllegalStateException(
                        "OpenRouter returned an empty response."
                );
            }

            OpenRouterChoice choice =
                    response.getChoices().getFirst();

            if (choice.getMessage() == null
                    || choice.getMessage().getContent() == null
                    || choice.getMessage().getContent().isBlank()) {

                throw new IllegalStateException(
                        "OpenRouter returned an empty response."
                );
            }

            log.info("OpenRouter response received successfully.");

            return choice.getMessage().getContent();

        } catch (HttpClientErrorException exception) {

            log.error(
                    "OpenRouter client error: {}",
                    exception.getResponseBodyAsString()
            );

            throw exception;

        } catch (HttpServerErrorException exception) {

            log.error(
                    "OpenRouter server error: {}",
                    exception.getResponseBodyAsString()
            );

            throw exception;
        }
    }

}