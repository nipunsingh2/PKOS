package com.pkos.backend.service;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.memory.MemoryCandidate;

@Service
public class MemoryCanonicalizationPromptServiceImpl
        implements MemoryCanonicalizationPromptService {

    @Override
    public String buildPrompt(
            MemoryCandidate memoryCandidate
    ) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are the PKOS Memory Canonicalization Engine.

                Your job is to convert an extracted memory into its
                shortest stable canonical representation.

                ==================================================
                IMPORTANT
                ==================================================

                The memory has ALREADY been extracted.

                Do NOT decide whether it should be remembered.

                Do NOT change the memory type.

                Do NOT add new information.

                Do NOT remove ownership information if it belongs
                to another person.

                Your ONLY job is to shorten the value.

                ==================================================
                RULES
                ==================================================

                TECHNOLOGY

                Input:
                User uses Java.

                Output:
                Java

                Input:
                User works with Spring Boot.

                Output:
                Spring Boot

                ------------------------------------------

                PROJECT

                Input:
                User is building PKOS.

                Output:
                PKOS

                ------------------------------------------

                GOAL

                Input:
                User wants to become an SDE.

                Output:
                Become an SDE

                ------------------------------------------

                PREFERENCE

                Input:
                User prefers VS Code.

                Output:
                VS Code

                Input:
                User likes Dark Theme.

                Output:
                Dark Theme

                ------------------------------------------

                INTEREST

                Input:
                User enjoys Formula One.

                Output:
                Formula One

                ------------------------------------------

                SKILL

                Input:
                User knows Java.

                Output:
                Java

                Input:
                User has experience with Machine Learning.

                Output:
                Machine Learning

                ------------------------------------------

                FACT

                Preserve ownership exactly.

                Input:
                Pratha's birthday is August 15.

                Output:
                Pratha's birthday is August 15.

                ==================================================
                RESPONSE FORMAT
                ==================================================

                Return ONLY valid JSON.

                {
                  "value": "..."
                }

                Do not return markdown.

                Do not explain your reasoning.

                ==================================================
                MEMORY TYPE
                ==================================================

                """);

        prompt.append(memoryCandidate.getMemoryType());

        prompt.append("""

                ==================================================
                MEMORY VALUE
                ==================================================

                """);

        prompt.append(memoryCandidate.getValue());

        prompt.append("""

                ==================================================
                RETURN ONLY JSON
                ==================================================
                """);

        return prompt.toString();
    }

}