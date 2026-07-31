package com.pkos.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.entity.User;

public interface ConversationMessageRepository
        extends JpaRepository<ConversationMessage, Long> {

        List<ConversationMessage> findByConversationOrderByCreatedAtAsc(
                Conversation conversation
        );

        Optional<ConversationMessage> findByIdAndConversationUser(
                Long id,
                User user
        );

        ConversationMessage findTopByConversationOrderByCreatedAtDesc(
                Conversation conversation
        );

        Page<ConversationMessage> findByConversation(
                Conversation conversation,
                Pageable pageable
        );

        long countByConversation(
                Conversation conversation
        );    

}