package com.pkos.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pkos.backend.dto.request.AiQuestionRequest;
import com.pkos.backend.dto.response.AiChatResponse;
import com.pkos.backend.dto.response.AiQuestionResponse;
import com.pkos.backend.dto.request.AiChatRequest;
import com.pkos.backend.service.AiAssistantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private static final long STREAM_TIMEOUT =
            300_000L;

    private final AiAssistantService aiAssistantService;

    @PostMapping("/ask")
    @ResponseStatus(HttpStatus.OK)
    public AiQuestionResponse askQuestion(
            @Valid @RequestBody AiQuestionRequest request) {

        return aiAssistantService.askQuestion(
                request.getQuestion()
        );
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public AiChatResponse chat(
            @Valid @RequestBody AiChatRequest request
    ) {

        return aiAssistantService.chat(request);
    }

    @PostMapping(
            value = "/chat/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamChat(
            @Valid @RequestBody AiChatRequest request
    ) {

        SseEmitter emitter =
                new SseEmitter(STREAM_TIMEOUT);

        try {

            emitter.send(
                    SseEmitter.event()
                            .name("connected")
                            .data("Stream connected")
            );

            var sources =
                    aiAssistantService.streamChat(
                            request,
                            chunk -> {

                                try {

                                    emitter.send(
                                            SseEmitter.event()
                                                    .name("token")
                                                    .data(chunk)
                                    );

                                } catch (Exception exception) {

                                    throw new IllegalStateException(
                                            "Failed to send streaming response.",
                                            exception
                                    );
                                }
                            }
                    );

            emitter.send(
                    SseEmitter.event()
                            .name("sources")
                            .data(sources)
            );

            emitter.send(
                    SseEmitter.event()
                            .name("complete")
                            .data("[DONE]")
            );

            emitter.complete();

        } catch (Exception exception) {

            try {

                emitter.send(
                        SseEmitter.event()
                                .name("error")
                                .data(
                                        "AI streaming failed."
                                )
                );

            } catch (Exception sendException) {

                emitter.completeWithError(
                        sendException
                );

                return emitter;
            }

            emitter.completeWithError(
                    exception
            );
        }

        return emitter;
    }
}