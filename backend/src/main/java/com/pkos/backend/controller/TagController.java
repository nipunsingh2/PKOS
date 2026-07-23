package com.pkos.backend.controller;

import com.pkos.backend.dto.request.CreateTagRequest;
import com.pkos.backend.dto.request.UpdateTagRequest;
import com.pkos.backend.dto.response.TagResponse;
import com.pkos.backend.service.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            @Valid @RequestBody CreateTagRequest request) {

        return ResponseEntity.ok(
                tagService.createTag(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return ResponseEntity.ok(
                tagService.getTags(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponse> getTagById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                tagService.getTagById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTagRequest request) {

        return ResponseEntity.ok(
                tagService.updateTag(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable Long id) {

        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}