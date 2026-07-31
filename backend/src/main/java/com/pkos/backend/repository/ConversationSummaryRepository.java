package com.pkos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationSummary;

public interface ConversationSummaryRepository
        extends JpaRepository<ConversationSummary, Long> {

    Optional<ConversationSummary> findByConversation(
            Conversation conversation
    );

}  