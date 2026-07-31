package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.entity.ConversationMessage;

public interface ConversationSummaryGeneratorService {

    String generateSummary(List<ConversationMessage> messages);

}