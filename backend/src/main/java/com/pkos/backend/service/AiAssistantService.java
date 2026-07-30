package com.pkos.backend.service;

import com.pkos.backend.dto.response.AiQuestionResponse;

public interface AiAssistantService {

    AiQuestionResponse askQuestion(String question);

}