package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.entity.Note;

public interface PromptBuilderService {

    String buildPrompt(
            String question,
            List<Note> notes
    );

}