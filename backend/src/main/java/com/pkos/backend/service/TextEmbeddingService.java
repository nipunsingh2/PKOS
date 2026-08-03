package com.pkos.backend.service;

public interface TextEmbeddingService {

    float[] generateEmbedding(
            String text
    );

}