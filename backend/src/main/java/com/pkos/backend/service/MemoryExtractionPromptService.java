package com.pkos.backend.service;

import java.util.List;

public interface MemoryExtractionPromptService {

    String buildPrompt(
            String conversationSummary,
            List<String> userMessages
    );

}