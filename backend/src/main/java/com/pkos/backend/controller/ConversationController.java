package com.pkos.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.pkos.backend.dto.request.CreateConversationRequest;
import com.pkos.backend.dto.request.UpdateConversationRequest;
import com.pkos.backend.dto.response.ConversationMessageResponse;
import com.pkos.backend.dto.response.ConversationResponse;
import com.pkos.backend.entity.Conversation;
import com.pkos.backend.mapper.ConversationMapper;
import com.pkos.backend.service.ConversationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    private final ConversationMapper conversationMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse createConversation(
            @Valid @RequestBody CreateConversationRequest request
    ) {

        Conversation conversation =
                conversationService.createConversation(
                        request.getTitle()
                );

        return conversationMapper.toResponse(conversation);
    }

    @GetMapping
    public List<ConversationResponse> getConversations() {

        return conversationService.getUserConversations();
    }

    @GetMapping("/{id}")
    public ConversationResponse getConversation(
            @PathVariable Long id
    ) {

        Conversation conversation =
                conversationService.getConversation(id);

        return conversationMapper.toResponse(conversation);
    }

    @PatchMapping("/{id}")
    public ConversationResponse renameConversation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateConversationRequest request
    ) {

        Conversation conversation =
                conversationService.renameConversation(
                        id,
                        request.getTitle()
                );

        return conversationMapper.toResponse(conversation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConversation(
            @PathVariable Long id
    ) {

        conversationService.deleteConversation(id);
    }

    @GetMapping("/{id}/messages")
    public List<ConversationMessageResponse> getConversationMessages(
            @PathVariable Long id
    ) {

        return conversationService.getConversationMessages(id);
    }

}