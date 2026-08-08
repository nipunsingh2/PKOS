package com.pkos.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RequiredArgsConstructor
public class OpenRouterLLMService
implements LLMService {

    private final OpenRouterProperties openRouterProperties;

    private final LlmRetryExecutor retryExecutor;

    private final ObjectMapper objectMapper;

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
                        .model(
                                openRouterProperties.getChatModel()
                        )
                        .messages(
                                List.of(
                                        OpenRouterMessage.builder()
                                                .role("user")
                                                .content(
                                                        request.getPrompt()
                                                )
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
                                                    openRouterProperties
                                                            .getBaseUrl()
                                                            + "/chat/completions"
                                            )
                                            .header(
                                                    HttpHeaders.AUTHORIZATION,
                                                    "Bearer "
                                                            + openRouterProperties
                                                                    .getApiKey()
                                            )
                                            .header(
                                                    "HTTP-Referer",
                                                    openRouterProperties
                                                            .getSiteUrl()
                                            )
                                            .header(
                                                    "X-OpenRouter-Title",
                                                    openRouterProperties
                                                            .getAppName()
                                            )
                                            .contentType(
                                                    MediaType.APPLICATION_JSON
                                            )
                                            .body(chatRequest)
                                            .retrieve()
                                            .body(
                                                    OpenRouterChatResponse.class
                                            )
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
                    || choice.getMessage()
                            .getContent()
                            .isBlank()) {

                throw new IllegalStateException(
                        "OpenRouter returned an empty response."
                );
            }

            log.info(
                    "OpenRouter response received successfully."
            );

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

    @Override
    public void streamResponse(
            LLMRequest request,
            Consumer<String> chunkConsumer
    ) {

        validateStreamingRequest(
                request,
                chunkConsumer
        );

        log.info(
                "Starting streaming request to OpenRouter using model: {}",
                openRouterProperties.getChatModel()
        );

        OpenRouterMessage message =
                OpenRouterMessage.builder()
                        .role("user")
                        .content(request.getPrompt())
                        .build();

        Map<String, Object> streamingRequest =
                Map.of(
                        "model",
                        openRouterProperties.getChatModel(),
                        "messages",
                        List.of(message),
                        "stream",
                        true
                );

        try {

            restClient
                    .method(HttpMethod.POST)
                    .uri(
                            openRouterProperties
                                    .getBaseUrl()
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
                    .contentType(
                            MediaType.APPLICATION_JSON
                    )
                    .accept(
                            MediaType.TEXT_EVENT_STREAM
                    )
                    .body(streamingRequest)
                    .exchange(
                            (clientRequest, clientResponse) -> {

                                if (clientResponse
                                        .getStatusCode()
                                        .is4xxClientError()) {

                                    throw HttpClientErrorException.create(
                                            clientResponse.getStatusCode(),
                                            clientResponse
                                                    .getStatusText(),
                                            clientResponse.getHeaders(),
                                            clientResponse
                                                    .getBody()
                                                    .readAllBytes(),
                                            StandardCharsets.UTF_8
                                    );
                                }

                                if (clientResponse
                                        .getStatusCode()
                                        .is5xxServerError()) {

                                    throw HttpServerErrorException.create(
                                            clientResponse.getStatusCode(),
                                            clientResponse
                                                    .getStatusText(),
                                            clientResponse.getHeaders(),
                                            clientResponse
                                                    .getBody()
                                                    .readAllBytes(),
                                            StandardCharsets.UTF_8
                                    );
                                }

                                try {
                                processStreamingResponse(
                                        clientResponse.getBody(),
                                        chunkConsumer
                                );
                                } catch (IOException exception) {
                                throw new IllegalStateException(
                                        "OpenRouter streaming connection failed.",
                                        exception
                                );
                                }

                                return null;
                            }
                    );

            log.info(
                    "OpenRouter streaming response completed successfully."
            );

        } catch (HttpClientErrorException exception) {

            log.error(
                    "OpenRouter streaming client error: {}",
                    exception.getResponseBodyAsString()
            );

            throw exception;

        } catch (HttpServerErrorException exception) {

            log.error(
                    "OpenRouter streaming server error: {}",
                    exception.getResponseBodyAsString()
            );

            throw exception;

        }
    }

    private void processStreamingResponse(
            java.io.InputStream inputStream,
            Consumer<String> chunkConsumer
    ) throws IOException {

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (!line.startsWith("data:")) {
                    continue;
                }

                String data =
                        line.substring("data:".length())
                                .trim();

                if ("[DONE]".equals(data)) {
                    return;
                }

                if (data.isBlank()) {
                    continue;
                }

                JsonNode root =
                        objectMapper.readTree(data);

                JsonNode choices =
                        root.path("choices");

                if (!choices.isArray()
                        || choices.isEmpty()) {
                    continue;
                }

                JsonNode delta =
                        choices.get(0).path("delta");

                JsonNode content =
                        delta.path("content");

                if (!content.isMissingNode()
                        && !content.isNull()
                        && content.isTextual()
                        && !content.asText().isEmpty()) {

                    chunkConsumer.accept(
                            content.asText()
                    );
                }
            }
        }
    }

    private void validateStreamingRequest(
            LLMRequest request,
            Consumer<String> chunkConsumer
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

        if (chunkConsumer == null) {
            throw new IllegalArgumentException(
                    "Chunk consumer cannot be null."
            );
        }
    }
}