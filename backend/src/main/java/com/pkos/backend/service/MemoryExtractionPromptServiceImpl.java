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

                Your job is to extract ONLY high-confidence, long-term memories
                from the user's messages.

                The extracted memories will become permanent knowledge inside
                the user's Personal Knowledge Operating System (PKOS).

                Accuracy is more important than recall.

                ==================================================
                OBJECTIVE
                ==================================================

                Extract only information that is likely to remain useful
                for weeks, months, or years.

                If uncertain, DO NOT extract it.

                ==================================================
                EXTRACT ONLY
                ==================================================

                Extract information such as:

                • Long-term projects
                • Long-term goals
                • Skills
                • Technologies regularly used
                • Programming languages
                • Stable preferences
                • Personal interests
                • Education
                • Occupation
                • Long-term personal facts

                ==================================================
                NEVER EXTRACT
                ==================================================

                Never extract:

                • Greetings
                • Small talk
                • Temporary requests
                • One-time questions
                • Assistant responses
                • Assumptions
                • Guesses
                • Information not explicitly stated
                • Duplicate information already present in the conversation summary

                ==================================================
                OWNERSHIP RULES
                ==================================================

                Preserve ownership EXACTLY.

                If the user says:

                "Pratha's birthday is August 15"

                output exactly:

                "Pratha's birthday is August 15"

                NEVER rewrite it as:

                "User's birthday is August 15"

                Never assume another person's facts belong to the user.

                If ownership is unclear, do not extract the memory.

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
                CANONICAL VALUE RULES
                ==================================================

                Store memories in their shortest stable form.

                PROJECT
                    Store only the project name.

                    Example:
                    PKOS

                TECHNOLOGY
                    Store only the technology name.

                    Examples:
                    Java
                    Spring Boot
                    PostgreSQL
                    Rust
                    Docker

                SKILL
                    Store only the skill.

                    Examples:
                    Java
                    Machine Learning
                    System Design

                INTEREST
                    Store only the interest.

                    Examples:
                    Psychology
                    Chess
                    Formula 1

                PREFERENCE
                    Store only the preferred item.

                    Good:
                    VS Code
                    Dark Theme
                    Vegetarian Diet

                    Bad:
                    "User prefers VS Code."

                GOAL
                    Store the goal as a concise long-term objective.

                    Good:
                    Become an SDE
                    Learn Kubernetes
                    Build PKOS

                FACT
                    Preserve the exact fact without changing ownership.

                    Good:
                    Pratha's birthday is August 15

                    Good:
                    User lives in Delhi

                    Bad:
                    User's birthday is August 15
                    (unless explicitly stated)

                ==================================================
                DUPLICATE RULES
                ==================================================

                If the conversation summary already contains
                the same memory, do not emit it again.

                Never generate paraphrased versions of the same memory.

                Use one canonical representation.

                ==================================================
                JSON FORMAT
                ==================================================

                Return ONLY valid JSON.

                Do NOT use markdown.

                Do NOT explain your reasoning.

                Do NOT output any text before or after the JSON.

                Your response must be directly parseable by Jackson.

                Valid format:

                {
                  "memories": [
                    {
                      "memoryType": "TECHNOLOGY",
                      "value": "Rust"
                    }
                  ]
                }

                If nothing should be extracted:

                {
                  "memories": []
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
                NEW USER MESSAGES
                ==================================================

                """);

        for (String message : userMessages) {

            prompt.append("- ")
                    .append(message.trim())
                    .append("\n");
        }

        prompt.append("""

                ==================================================
                FINAL REMINDER
                ==================================================

                Return ONLY valid JSON.

                Never invent facts.

                Never change ownership.

                Never paraphrase memories.

                Use the canonical value rules above.

                """);

        return prompt.toString();
    }

}