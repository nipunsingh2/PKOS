package com.pkos.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pkos.backend.dto.response.MemoryResponse;
import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.MemoryMapper;
import com.pkos.backend.service.CurrentUserService;
import com.pkos.backend.service.MemoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/memories")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    private final CurrentUserService currentUserService;

    private final MemoryMapper memoryMapper;

    @GetMapping
    public ResponseEntity<List<MemoryResponse>> getMemories() {

        User currentUser = currentUserService.getCurrentUser();

        List<MemoryResponse> memories =
                memoryService.getMemories(currentUser)
                        .stream()
                        .map(memoryMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(memories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoryResponse> getMemory(
            @PathVariable Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Memory memory =
                memoryService.getMemoryById(
                        currentUser,
                        id
                );

        return ResponseEntity.ok(
                memoryMapper.toResponse(memory)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemory(
            @PathVariable Long id) {

        User currentUser = currentUserService.getCurrentUser();

        memoryService.deleteMemory(
                currentUser,
                id
        );

        return ResponseEntity.noContent().build();
    }

}