package com.pkos.backend.service.graph;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import com.pkos.backend.dto.response.RelatedNoteResponse;
import com.pkos.backend.mapper.RelatedNoteMapper;
import com.pkos.backend.entity.KnowledgeRelationship;
import com.pkos.backend.entity.enums.KnowledgeNodeType;
import com.pkos.backend.entity.enums.RelationshipType;
import com.pkos.backend.repository.KnowledgeRelationshipRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    private final KnowledgeRelationshipRepository repository;
    private final RelatedNoteMapper relatedNoteMapper;

    @Override
    public KnowledgeRelationship createRelationship(
            KnowledgeNodeType sourceType,
            Long sourceId,
            KnowledgeNodeType targetType,
            Long targetId,
            RelationshipType relationshipType,
            double confidence) {

        if (relationshipExists(
                sourceType,
                sourceId,
                targetType,
                targetId,
                relationshipType)
        || relationshipExists(
                targetType,
                targetId,
                sourceType,
                sourceId,
                relationshipType)) {

        throw new IllegalArgumentException(
                "Relationship already exists.");
        }

        KnowledgeRelationship relationship = new KnowledgeRelationship();

        relationship.setSourceType(sourceType);
        relationship.setSourceId(sourceId);

        relationship.setTargetType(targetType);
        relationship.setTargetId(targetId);

        relationship.setRelationshipType(relationshipType);
        relationship.setConfidence(confidence);

        return repository.save(relationship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeRelationship> getOutgoingRelationships(
            KnowledgeNodeType sourceType,
            Long sourceId) {

        return repository.findBySourceTypeAndSourceId(
                sourceType,
                sourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeRelationship> getIncomingRelationships(
            KnowledgeNodeType targetType,
            Long targetId) {

        return repository.findByTargetTypeAndTargetId(
                targetType,
                targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean relationshipExists(
            KnowledgeNodeType sourceType,
            Long sourceId,
            KnowledgeNodeType targetType,
            Long targetId,
            RelationshipType relationshipType) {

        return repository
                .existsBySourceTypeAndSourceIdAndTargetTypeAndTargetIdAndRelationshipType(
                        sourceType,
                        sourceId,
                        targetType,
                        targetId,
                        relationshipType);
    }

        @Override
        public List<RelatedNoteResponse> getRelatedNotes(Long noteId) {

        return repository
                .findBySourceTypeAndSourceIdAndRelationshipTypeOrderByConfidenceDesc(
                        KnowledgeNodeType.NOTE,
                        noteId,
                        RelationshipType.RELATED)
                .stream()
                .map(relatedNoteMapper::toResponse)
                .collect(Collectors.toList());
        }

}