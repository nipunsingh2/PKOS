package com.pkos.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pkos.backend.dto.request.AiQuestionRequest;
import com.pkos.backend.dto.response.AiQuestionResponse;
import com.pkos.backend.service.AiAssistantService;
import com.pkos.backend.dto.request.AiChatRequest;
import com.pkos.backend.dto.response.AiChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAssistantService aiAssistantService;

    @PostMapping("/ask")
    @ResponseStatus(HttpStatus.OK)
    public AiQuestionResponse askQuestion(
            @Valid @RequestBody AiQuestionRequest request) {

        return aiAssistantService.askQuestion(request.getQuestion());
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public AiChatResponse chat(
            @Valid @RequestBody AiChatRequest request
    ) {

        return aiAssistantService.chat(request);
    }

}