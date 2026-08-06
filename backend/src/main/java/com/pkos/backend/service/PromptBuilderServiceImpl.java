package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.Note;

@Service
public class PromptBuilderServiceImpl implements PromptBuilderService {

    @Override
    public String buildPrompt(
            String question,
            List<Note> notes
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are PKOS AI Assistant.

                You answer questions ONLY using the user's notes.

                If the answer cannot be found in the notes,
                clearly say that the information is not available.

                Never invent facts.

                ----------------------------
                USER NOTES
                ----------------------------

                """);

        for (Note note : notes) {

            prompt.append("Title: ")
                    .append(note.getTitle())
                    .append("\n");

            prompt.append("Content:\n")
                    .append(note.getContent())
                    .append("\n\n");
        }

        prompt.append("""
                ----------------------------

                USER QUESTION:
                """);

        prompt.append(question);

        prompt.append("""

                ----------------------------

                Provide a clear, structured answer.
                """);

        return prompt.toString();
    }


        @Override
        public String buildConversationPrompt(
                String conversationSummary,
                List<Memory> memories,
                List<Note> notes,
                List<ConversationMessage> conversationHistory,
                String userQuestion
        ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are PKOS AI Assistant.

                You are continuing an ongoing conversation with the user.

                Maintain a natural and helpful conversation using the conversation history.

                When answering factual questions, use the user's notes as the primary source of truth.

                If the answer is not present in the user's notes, clearly state that you do not have enough information instead of guessing.

                Never fabricate facts.

                """);

        if (conversationSummary != null && !conversationSummary.isBlank()) {

                prompt.append("""
                        ----------------------------
                        CONVERSATION SUMMARY
                        ----------------------------

                        """);

                prompt.append(conversationSummary);
                prompt.append("\n\n");
        }

        if (memories != null && !memories.isEmpty()) {

        prompt.append("""
                ----------------------------
                LONG-TERM USER MEMORY
                ----------------------------

                The following information has been learned from previous
                conversations with the user.

                Use these memories only when they are relevant to the
                current question. Do not mention them unless they help
                answer naturally.

                """);

        for (Memory memory : memories) {

                prompt.append("- ")
                        .append(memory.getMemoryType())
                        .append(": ")
                        .append(memory.getValue())
                        .append("\n");
        }

        prompt.append("\n");
        }

        prompt.append("""
                ----------------------------
                RECENT CONVERSATION
                ----------------------------

                """);

        for (ConversationMessage message : conversationHistory) {

                String role = switch (message.getRole()) {
                case USER -> "User";
                case ASSISTANT -> "Assistant";
                case SYSTEM -> "System";
                };

                prompt.append(role)
                        .append(": ")
                        .append(message.getContent())
                        .append("\n");
        }

        prompt.append("""

                ----------------------------
                USER NOTES
                ----------------------------

                """);

        for (Note note : notes) {

        if (note.getContent() == null ||
                note.getContent().isBlank()) {
                continue;
        }

        prompt.append("Title: ")
                .append(note.getTitle())
                .append("\n");

        prompt.append("Content:\n")
                .append(note.getContent().trim())
                .append("\n\n");
        }

        prompt.append("""
                ----------------------------
                USER QUESTION
                ----------------------------

                """);

        prompt.append(userQuestion);

        prompt.append("""

                ----------------------------

                Continue the conversation naturally.

                Use:
                1. The conversation summary (if available).
                2. The user's long-term memories when they are relevant.
                3. The user's notes as the primary source of truth.
                4. The recent conversation history.

                Do not mention long-term memories unless they naturally
                improve your answer.

                If the notes do not contain enough information to answer a factual question,
                clearly state that instead of making up information.

                """);

        return prompt.toString();
        }

}