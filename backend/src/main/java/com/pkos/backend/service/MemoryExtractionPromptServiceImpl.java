package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MemoryExtractionPromptServiceImpl
        implements MemoryExtractionPromptService {

    @Override
    public String buildPrompt(
            String conversationSummary,
            List<String> userMessages
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are the PKOS Memory Extraction Engine.

                Your task is to identify ONLY information that should become the
                user's long-term memory.

                A memory should represent information that is expected to remain
                useful for weeks, months, or years.

                ==================================================
                WHAT TO EXTRACT
                ==================================================

                Extract memories such as:

                • Current projects
                • Long-term goals
                • Skills
                • Technologies the user regularly uses
                • Programming languages
                • Personal interests
                • Stable preferences
                • Education
                • Occupation
                • Long-term personal facts

                ==================================================
                DO NOT EXTRACT
                ==================================================

                Never extract:

                • Greetings
                • Small talk
                • Thank-you messages
                • Temporary questions
                • One-time requests
                • Casual conversation
                • Information already contained in the conversation summary
                • Duplicate memories

                ==================================================
                MEMORY TYPES
                ==================================================

                Use ONLY one of these values:

                PROJECT
                GOAL
                SKILL
                TECHNOLOGY
                PREFERENCE
                INTEREST
                FACT

                ==================================================
                OUTPUT FORMAT
                ==================================================

                Return ONLY valid JSON.

                Do NOT wrap the JSON inside markdown.

                Do NOT explain your reasoning.

                Return exactly this structure:

                {
                  "memories": [
                    {
                      "memoryType": "PROJECT",
                      "value": "PKOS"
                    }
                  ]
                }

                If no memories should be extracted, return:

                {
                  "memories": []
                }

                ==================================================
                EXAMPLE
                ==================================================

                User Messages:

                - I'm building PKOS using Spring Boot and PostgreSQL.
                - I prefer VS Code.

                Output:

                {
                  "memories": [
                    {
                      "memoryType": "PROJECT",
                      "value": "PKOS"
                    },
                    {
                      "memoryType": "TECHNOLOGY",
                      "value": "Spring Boot"
                    },
                    {
                      "memoryType": "TECHNOLOGY",
                      "value": "PostgreSQL"
                    },
                    {
                      "memoryType": "PREFERENCE",
                      "value": "VS Code"
                    }
                  ]
                }

                ==================================================
                CONVERSATION SUMMARY
                ==================================================

                """);

        if (conversationSummary != null
                && !conversationSummary.isBlank()) {

            prompt.append(conversationSummary);

        } else {

            prompt.append("None");
        }

        prompt.append("""

                ==================================================
                USER MESSAGES
                ==================================================

                """);

        for (String message : userMessages) {

            prompt.append("- ")
                    .append(message.trim())
                    .append("\n");
        }

        return prompt.toString();
    }
}