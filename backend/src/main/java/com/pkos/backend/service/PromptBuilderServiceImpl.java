package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

}