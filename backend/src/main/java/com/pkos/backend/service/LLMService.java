package com.pkos.backend.service;

import java.util.function.Consumer;

import com.pkos.backend.dto.llm.LLMRequest;

public interface LLMService {

    String generateResponse(
            LLMRequest request
    );

    void streamResponse(
            LLMRequest request,
            Consumer<String> chunkConsumer
    );

}