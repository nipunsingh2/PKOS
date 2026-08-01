package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.llm.LLMRequest;
import com.pkos.backend.entity.ConversationMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationSummaryGeneratorServiceImpl
        implements ConversationSummaryGeneratorService {

        private final LLMService llmService;

    @Override
    public String generateSummary(
            List<ConversationMessage> messages
    ) {

        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException(
                    "Conversation messages cannot be null or empty."
            );
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are creating a long-term memory summary for an AI assistant.

Summarize the following conversation.

Requirements:
- Keep the summary concise (200-400 words).
- Preserve important user preferences.
- Preserve goals and ongoing tasks.
- Preserve important decisions.
- Preserve relevant facts.
- Remove greetings and small talk.
- Write in third person.
- Return only the summary.

Conversation:

""");

        for (ConversationMessage message : messages) {

            prompt.append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append("\n");
        }

        return llmService.generateResponse(
                LLMRequest.builder()
                        .prompt(prompt.toString())
                        .build()
        );
    }

}