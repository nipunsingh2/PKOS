package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.AiQuestionResponse;
import com.pkos.backend.dto.response.SourceNoteResponse;
import com.pkos.backend.entity.Note;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements AiAssistantService {

    private final SemanticRetrievalService semanticRetrievalService;

    private final PromptBuilderService promptBuilderService;

    private final GeminiChatService geminiChatService;

    @Override
    public AiQuestionResponse askQuestion(String question) {

        List<Note> notes =
                semanticRetrievalService.retrieveRelevantNotes(question);

        String prompt =
                promptBuilderService.buildPrompt(question, notes);

        String answer =
                geminiChatService.generateResponse(prompt);

        List<SourceNoteResponse> sources = notes.stream()
                .map(note -> SourceNoteResponse.builder()
                        .id(note.getId())
                        .title(note.getTitle())
                        .build())
                .toList();

        return AiQuestionResponse.builder()
                .answer(answer)
                .sources(sources)
                .build();
    }

}