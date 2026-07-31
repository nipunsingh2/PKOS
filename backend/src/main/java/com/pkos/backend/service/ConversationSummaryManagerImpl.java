package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.ConversationSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationSummaryManagerImpl
        implements ConversationSummaryManager {

    private static final int SUMMARY_INTERVAL = 20;

    private final ConversationService conversationService;

    private final ConversationSummaryService conversationSummaryService;

    private final ConversationSummaryGeneratorService
            conversationSummaryGeneratorService;

    @Override
    public void updateSummaryIfRequired(
            Conversation conversation
    ) {

        long currentMessageCount =
                conversationService.countMessages(
                        conversation
                );

        ConversationSummary existingSummary =
                conversationSummaryService
                        .getSummary(conversation)
                        .orElse(null);

        /*
         * First summary:
         * Generate once the conversation exceeds
         * the recent-history window.
         */
        if (existingSummary == null) {

            if (currentMessageCount <= SUMMARY_INTERVAL) {
                return;
            }

            List<ConversationMessage> messages =
                    conversationService.getConversationHistory(
                            conversation.getId()
                    );

            generateAndSaveSummary(
                    conversation,
                    messages,
                    (int) currentMessageCount
            );

            return;
        }

        int summarizedMessageCount =
                existingSummary.getSummarizedMessageCount();

        if (currentMessageCount - summarizedMessageCount
                < SUMMARY_INTERVAL) {
            return;
        }

        List<ConversationMessage> messages =
                conversationService.getConversationHistory(
                        conversation.getId()
                );

        generateAndSaveSummary(
                conversation,
                messages,
                (int) currentMessageCount
        );
    }

    private void generateAndSaveSummary(
            Conversation conversation,
            List<ConversationMessage> messages,
            int summarizedMessageCount
    ) {

        String summary =
                conversationSummaryGeneratorService
                        .generateSummary(messages);

        conversationSummaryService.saveSummary(
                conversation,
                summary,
                summarizedMessageCount
        );
    }

}