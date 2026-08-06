package com.pkos.backend.service.graph;

import java.util.List;

import com.pkos.backend.entity.KnowledgeRelationship;
import com.pkos.backend.entity.enums.KnowledgeNodeType;
import com.pkos.backend.entity.enums.RelationshipType;
import com.pkos.backend.dto.response.RelatedNoteResponse;

public interface KnowledgeGraphService {

    KnowledgeRelationship createRelationship(
            KnowledgeNodeType sourceType,
            Long sourceId,
            KnowledgeNodeType targetType,
            Long targetId,
            RelationshipType relationshipType,
            double confidence);

    List<KnowledgeRelationship> getOutgoingRelationships(
            KnowledgeNodeType sourceType,
            Long sourceId);

    List<KnowledgeRelationship> getIncomingRelationships(
            KnowledgeNodeType targetType,
            Long targetId);

    boolean relationshipExists(
            KnowledgeNodeType sourceType,
            Long sourceId,
            KnowledgeNodeType targetType,
            Long targetId,
            RelationshipType relationshipType);

    List<RelatedNoteResponse> getRelatedNotes(Long noteId);
}