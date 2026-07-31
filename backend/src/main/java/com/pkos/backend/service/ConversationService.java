package com.pkos.backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pkos.backend.entity.User;
import com.pkos.backend.dto.response.ConversationMessageResponse;
import com.pkos.backend.dto.response.ConversationResponse;
import com.pkos.backend.entity.Conversation;
import com.pkos.backend.entity.ConversationMessage;
import com.pkos.backend.mapper.ConversationMapper;
import com.pkos.backend.mapper.ConversationMessageMapper;
import com.pkos.backend.repository.ConversationMessageRepository;
import com.pkos.backend.repository.ConversationRepository;
import com.pkos.backend.entity.MessageRole;
import com.pkos.backend.exception.ConversationNotFoundException;
import com.pkos.backend.exception.ResourceNotFoundException;

@Service
@Transactional
public class ConversationService {

    private static final Logger logger =
            LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;

    private final ConversationMessageRepository conversationMessageRepository;

    private final ConversationMapper conversationMapper;

    private final ConversationMessageMapper conversationMessageMapper;

    private final CurrentUserService currentUserService;

    private final AuditService auditService;

    public ConversationService(
            ConversationRepository conversationRepository,
            ConversationMessageRepository conversationMessageRepository,
            ConversationMapper conversationMapper,
            ConversationMessageMapper conversationMessageMapper,
            CurrentUserService currentUserService,
            AuditService auditService) {

        this.conversationRepository = conversationRepository;
        this.conversationMessageRepository = conversationMessageRepository;
        this.conversationMapper = conversationMapper;
        this.conversationMessageMapper = conversationMessageMapper;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    private Conversation findOwnedConversation(
            Long conversationId
    ) {

        User currentUser = currentUserService.getCurrentUser();

        return conversationRepository
                .findByIdAndUser(
                        conversationId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conversation not found with ID: "
                                        + conversationId
                        )
                );
    }

    private String defaultConversationTitle() {

        return "New Conversation";
    }


    private ConversationMessage saveMessage(
            Conversation conversation,
            MessageRole role,
            String content
    ) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "Message content cannot be blank."
            );
        }

        content = content.trim();

        logger.info(
                "Saving {} message for conversation {}",
                role,
                conversation.getId()
        );

        ConversationMessage message = ConversationMessage.builder()
                .conversation(conversation)
                .role(role)
                .content(content)
                .build();

        ConversationMessage savedMessage =
                conversationMessageRepository.save(message);

        logger.info(
                "Conversation message {} saved successfully.",
                savedMessage.getId()
        );

        return savedMessage;
    }

    private ConversationResponse buildConversationResponse(
            Conversation conversation
    ) {

        ConversationMessage lastMessage =
                conversationMessageRepository
                        .findTopByConversationOrderByCreatedAtDesc(
                                conversation
                        );

        return ConversationResponse.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .lastMessage(
                        lastMessage != null
                                ? lastMessage.getContent()
                                : null
                )
                .lastMessageRole(
                        lastMessage != null
                                ? lastMessage.getRole()
                                : null
                )
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    public Conversation createConversation(String title) {

        User currentUser = currentUserService.getCurrentUser();

        logger.info(
                "Creating conversation for user: {}",
                currentUser.getEmail()
        );

        Conversation conversation = Conversation.builder()
                .title(
                        title == null || title.isBlank()
                                ? defaultConversationTitle()
                                : title.trim()
                )
                .user(currentUser)
                .build();

        Conversation savedConversation =
                conversationRepository.save(conversation);

        auditService.logEvent(
                "Created Conversation",
                currentUser.getEmail()
        );

        logger.info(
                "Conversation created successfully. Conversation ID: {}, User: {}",
                savedConversation.getId(),
                currentUser.getEmail()
        );

        return savedConversation;
    }

    @Transactional(readOnly = true)
    public Conversation getConversation(
            Long conversationId
    ) {

        logger.info(
                "Fetching conversation with ID: {}",
                conversationId
        );

        Conversation conversation =
                findOwnedConversation(conversationId);

        logger.info(
                "Conversation {} fetched successfully.",
                conversationId
        );

        return conversation;
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations() {

        User currentUser = currentUserService.getCurrentUser();

        logger.info(
                "Fetching conversations for user: {}",
                currentUser.getEmail()
        );

        List<ConversationResponse> conversations =
                conversationRepository
                        .findByUserOrderByUpdatedAtDesc(currentUser)
                        .stream()
                        .map(this::buildConversationResponse)
                        .toList();

        logger.info(
                "Fetched {} conversations for user: {}",
                conversations.size(),
                currentUser.getEmail()
        );

        return conversations;
    }

    public Conversation renameConversation(
            Long conversationId,
            String title
    ) {

        Conversation conversation =
                findOwnedConversation(conversationId);

        User currentUser =
                currentUserService.getCurrentUser();

        String newTitle =
                title == null || title.isBlank()
                        ? defaultConversationTitle()
                        : title.trim();

        logger.info(
                "Renaming conversation {} for user: {}",
                conversationId,
                currentUser.getEmail()
        );

        conversation.setTitle(newTitle);

        Conversation updatedConversation =
                conversationRepository.save(conversation);

        auditService.logEvent(
                "Renamed Conversation",
                currentUser.getEmail()
        );

        logger.info(
                "Conversation {} renamed successfully.",
                conversationId
        );

        return updatedConversation;
    }

    public void deleteConversation(
            Long conversationId
    ) {

        Conversation conversation =
                findOwnedConversation(conversationId);

        User currentUser =
                currentUserService.getCurrentUser();

        logger.info(
                "Deleting conversation {} for user: {}",
                conversationId,
                currentUser.getEmail()
        );

        conversationRepository.delete(conversation);

        auditService.logEvent(
                "Deleted Conversation",
                currentUser.getEmail()
        );

        logger.info(
                "Conversation {} deleted successfully.",
                conversationId
        );
    }

    public ConversationMessage appendUserMessage(
            Conversation conversation,
            String content
    ) {

        return saveMessage(
                conversation,
                MessageRole.USER,
                content
        );
    }

    public ConversationMessage appendAssistantMessage(
            Conversation conversation,
            String content
    ) {

        return saveMessage(
                conversation,
                MessageRole.ASSISTANT,
                content
        );
    }


    @Transactional(readOnly = true)
    public List<ConversationMessageResponse> getConversationMessages(
            Long conversationId
    ) {

        Conversation conversation =
                findOwnedConversation(conversationId);

        logger.info(
                "Fetching messages for conversation {}",
                conversationId
        );

        List<ConversationMessageResponse> messages =
                conversationMessageRepository
                        .findByConversationOrderByCreatedAtAsc(conversation)
                        .stream()
                        .map(conversationMessageMapper::toResponse)
                        .toList();

        logger.info(
                "Fetched {} messages for conversation {}",
                messages.size(),
                conversationId
        );

        return messages;
    }

}