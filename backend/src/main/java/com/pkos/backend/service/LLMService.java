package com.pkos.backend.service;

import com.pkos.backend.dto.llm.LLMRequest;

public interface LLMService {

    String generateResponse(
            LLMRequest request
    );

}