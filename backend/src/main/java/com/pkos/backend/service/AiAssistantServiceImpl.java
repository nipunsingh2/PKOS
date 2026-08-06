package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.User;
import com.pkos.backend.dto.llm.LLMRequest;
import com.pkos.backend.dto.request.AiChatRequest;
import com.pkos.backend.dto.response.AiChatResponse;
import com.pkos.backend.dto.response.AiQuestionResponse;
import com.pkos.backend.dto.response.SourceNoteResponse;
import com.pkos.backend.dto.search.HybridSearchResult;
import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.ConversationSummary;
import com.pkos.backend.entity.Note;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiAssistantServiceImpl implements AiAssistantService {

    private final HybridSearchService hybridSearchService;

    private final PromptBuilderService promptBuilderService;

    private final LLMService llmService;

    private final ConversationService conversationService;

    private final ConversationSummaryService conversationSummaryService;

    private final ConversationSummaryManager conversationSummaryManager;

    private final MemoryManager memoryManager;

    private final EmbeddingService embeddingService;

    private final MemorySimilarityService memorySimilarityService;


    @Override
    public AiQuestionResponse askQuestion(String question) {

        List<HybridSearchResult> hybridResults =
                hybridSearchService.search(question);

        List<Note> notes = hybridResults.stream()
                .map(HybridSearchResult::getNote)
                .toList();

        String prompt =
                promptBuilderService.buildPrompt(question, notes);

        String answer =
                llmService.generateResponse(
                        LLMRequest.builder()
                                .prompt(prompt)
                                .build()
                );

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

    @Override
    public AiChatResponse chat(
            AiChatRequest request
    ) {

        Conversation conversation =
                conversationService.getConversation(
                        request.getConversationId()
                );

        conversationService.appendUserMessage(
                conversation,
                request.getMessage()
        );

        List<ConversationMessage> conversationHistory =
                conversationService.getRecentConversationHistory(
                        conversation.getId(),
                        20
                );

        List<HybridSearchResult> hybridResults =
                hybridSearchService.search(
                        request.getMessage()
                );

        List<Note> notes = hybridResults.stream()
                .map(HybridSearchResult::getNote)
                .toList();

        User currentUser = conversation.getUser();
        float[] queryEmbedding =
                embeddingService.generateEmbedding(
                        request.getMessage()
                );


        List<Memory> memories =
                memorySimilarityService.findTopRelevant(
                        currentUser,
                        queryEmbedding
                );

        String conversationSummary =
                conversationSummaryService
                        .getSummary(conversation)
                        .map(ConversationSummary::getSummary)
                        .orElse(null);

        String prompt =
                promptBuilderService.buildConversationPrompt(
                        conversationSummary,
                        memories,
                        notes,
                        conversationHistory,
                        request.getMessage()
                );

        log.debug(
                "Generated prompt for conversation {} ({} characters)",
                conversation.getId(),
                prompt.length()
        );
                
        String answer =
                llmService.generateResponse(
                        LLMRequest.builder()
                                .prompt(prompt)
                                .build()
                );

        conversationService.appendAssistantMessage(
                conversation,
                answer
        );

        conversationSummaryManager.updateSummaryIfRequired(
                conversation
        );

        memoryManager.processConversation(
                conversation
        );

        List<SourceNoteResponse> sources = notes.stream()
                .map(note -> SourceNoteResponse.builder()
                        .id(note.getId())
                        .title(note.getTitle())
                        .build())
                .toList();

        return AiChatResponse.builder()
                .conversationId(conversation.getId())
                .answer(answer)
                .sources(sources)
                .build();
    }

}