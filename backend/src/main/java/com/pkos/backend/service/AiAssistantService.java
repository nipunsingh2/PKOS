package com.pkos.backend.service;

import com.pkos.backend.dto.request.AiChatRequest;
import com.pkos.backend.dto.response.AiChatResponse;
import com.pkos.backend.dto.response.AiQuestionResponse;

public interface AiAssistantService {

    AiQuestionResponse askQuestion(String question);

    AiChatResponse chat(AiChatRequest request);

}