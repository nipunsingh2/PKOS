package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.RelatedNoteResponse;
import com.pkos.backend.entity.KnowledgeRelationship;
import com.pkos.backend.entity.Note;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RelatedNoteMapper {

    private final NoteRepository noteRepository;

    public RelatedNoteResponse toResponse(
            KnowledgeRelationship relationship) {

        Note targetNote = noteRepository
                .findById(relationship.getTargetId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Related note not found: "
                                        + relationship.getTargetId()));

        return RelatedNoteResponse.builder()
                .noteId(targetNote.getId())
                .title(targetNote.getTitle())
                .relationshipType(
                        relationship.getRelationshipType())
                .confidence(
                        relationship.getConfidence())
                .build();
    }
}