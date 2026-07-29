CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE note_embeddings (

    id BIGSERIAL PRIMARY KEY,

    note_id BIGINT NOT NULL,

    embedding_model VARCHAR(100) NOT NULL,

    embedding_version INTEGER NOT NULL,

    embedding VECTOR(768) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_note_embeddings_note
        UNIQUE (note_id),

    CONSTRAINT fk_note_embeddings_note
        FOREIGN KEY (note_id)
        REFERENCES notes(id)
        ON DELETE CASCADE

);