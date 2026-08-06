package com.pkos.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.KnowledgeRelationship;
import com.pkos.backend.entity.enums.KnowledgeNodeType;
import com.pkos.backend.entity.enums.RelationshipType;

public interface KnowledgeRelationshipRepository
        extends JpaRepository<KnowledgeRelationship, Long> {

    List<KnowledgeRelationship> findBySourceTypeAndSourceId(
            KnowledgeNodeType sourceType,
            Long sourceId);

    List<KnowledgeRelationship> findByTargetTypeAndTargetId(
            KnowledgeNodeType targetType,
            Long targetId);

    List<KnowledgeRelationship> findByRelationshipType(
            RelationshipType relationshipType);

    List<KnowledgeRelationship> findBySourceTypeAndSourceIdAndRelationshipType(
            KnowledgeNodeType sourceType,
            Long sourceId,
            RelationshipType relationshipType);

    boolean existsBySourceTypeAndSourceIdAndTargetTypeAndTargetIdAndRelationshipType(
            KnowledgeNodeType sourceType,
            Long sourceId,
            KnowledgeNodeType targetType,
            Long targetId,
            RelationshipType relationshipType);
        

        List<KnowledgeRelationship> findBySourceTypeAndSourceIdAndRelationshipTypeOrderByConfidenceDesc(
                KnowledgeNodeType sourceType,
                Long sourceId,
                RelationshipType relationshipType);
}