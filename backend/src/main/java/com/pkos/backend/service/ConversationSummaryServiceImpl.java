package com.pkos.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationSummary;
import com.pkos.backend.repository.ConversationSummaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationSummaryServiceImpl
        implements ConversationSummaryService {

    private final ConversationSummaryRepository
            conversationSummaryRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ConversationSummary> getSummary(
            Conversation conversation
    ) {

        return conversationSummaryRepository
                .findByConversation(conversation);
    }

    @Override
    public ConversationSummary saveSummary(
            Conversation conversation,
            String summary,
            int summarizedMessageCount
    ) {

        ConversationSummary conversationSummary =
                conversationSummaryRepository
                        .findByConversation(conversation)
                        .orElse(
                                ConversationSummary.builder()
                                        .conversation(conversation)
                                        .memoryProcessedMessageCount(0)
                                        .build()
                        );

        conversationSummary.setSummary(summary);
        conversationSummary.setSummarizedMessageCount(
                summarizedMessageCount
        );

        return conversationSummaryRepository.save(
                conversationSummary
        );
    }

    @Override
    public ConversationSummary updateMemoryProcessedCount(
            Conversation conversation,
            int memoryProcessedMessageCount
    ) {

        ConversationSummary conversationSummary =
                conversationSummaryRepository
                        .findByConversation(conversation)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Conversation summary not found."
                                )
                        );

        conversationSummary.setMemoryProcessedMessageCount(
                memoryProcessedMessageCount
        );

        return conversationSummaryRepository.save(
                conversationSummary
        );
    }

}