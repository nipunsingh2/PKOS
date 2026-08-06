-- =====================================================
-- KNOWLEDGE RELATIONSHIPS
-- =====================================================

CREATE TABLE knowledge_relationships (

    id BIGSERIAL PRIMARY KEY,

    source_type VARCHAR(30) NOT NULL,

    source_id BIGINT NOT NULL,

    target_type VARCHAR(30) NOT NULL,

    target_id BIGINT NOT NULL,

    relationship_type VARCHAR(30) NOT NULL,

    confidence DOUBLE PRECISION NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_knowledge_relationship_source
ON knowledge_relationships(source_type, source_id);

CREATE INDEX idx_knowledge_relationship_target
ON knowledge_relationships(target_type, target_id);

CREATE INDEX idx_knowledge_relationship_type
ON knowledge_relationships(relationship_type);

CREATE INDEX idx_knowledge_relationship_confidence
ON knowledge_relationships(confidence);