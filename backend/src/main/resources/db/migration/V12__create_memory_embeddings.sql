CREATE TABLE memory_embeddings (

    memory_id BIGINT PRIMARY KEY,

    embedding VECTOR(3072) NOT NULL,

    embedding_model VARCHAR(100) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_memory_embedding_memory
        FOREIGN KEY (memory_id)
        REFERENCES memories(id)
        ON DELETE CASCADE

);