package com.pkos.backend.service;

import java.util.List;
import java.util.function.Consumer;

import com.pkos.backend.dto.request.AiChatRequest;
import com.pkos.backend.dto.response.AiChatResponse;
import com.pkos.backend.dto.response.AiQuestionResponse;
import com.pkos.backend.dto.response.SourceNoteResponse;

public interface AiAssistantService {

    AiQuestionResponse askQuestion(
            String question
    );

    AiChatResponse chat(
            AiChatRequest request
    );

    List<SourceNoteResponse> streamChat(
            AiChatRequest request,
            Consumer<String> chunkConsumer
    );

}