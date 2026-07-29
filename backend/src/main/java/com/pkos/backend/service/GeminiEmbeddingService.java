package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentResponse;
import com.pkos.backend.config.GeminiProperties;

@Service
public class GeminiEmbeddingService implements EmbeddingService {

    private final Client client;
    private final GeminiProperties geminiProperties;

    public GeminiEmbeddingService(Client client,
                                  GeminiProperties geminiProperties) {
        this.client = client;
        this.geminiProperties = geminiProperties;
    }

    @Override
    public float[] generateEmbedding(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or blank.");
        }

        EmbedContentResponse response = client.models.embedContent(
                geminiProperties.getEmbeddingModel(),
                text,
                null
        );

        List<ContentEmbedding> embeddings = response.embeddings()
                .orElseThrow(() ->
                        new IllegalStateException("Gemini returned no embeddings."));

        if (embeddings.isEmpty()) {
            throw new IllegalStateException("Gemini returned an empty embedding list.");
        }

        List<Float> values = embeddings.get(0)
                .values()
                .orElseThrow(() ->
                        new IllegalStateException("Embedding vector is missing."));

        float[] vector = new float[values.size()];

        for (int i = 0; i < values.size(); i++) {
            vector[i] = values.get(i);
        }

        return vector;
    }
}