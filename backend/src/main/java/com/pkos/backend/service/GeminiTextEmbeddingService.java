package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.ContentEmbedding;
import com.google.genai.types.EmbedContentResponse;
import com.pkos.backend.config.GeminiProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiTextEmbeddingService
        implements TextEmbeddingService {

    private final Client client;

    private final GeminiProperties geminiProperties;

    @Override
    public float[] generateEmbedding(
            String text
    ) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text cannot be null or blank."
            );
        }

        EmbedContentResponse response =
                client.models.embedContent(
                        geminiProperties.getEmbeddingModel(),
                        text,
                        null
                );

        List<ContentEmbedding> embeddings =
                response.embeddings()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Gemini returned an empty embedding."
                                )
                        );

        if (embeddings.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned an empty embedding."
            );
        }

        List<Float> values =
                embeddings.getFirst()
                        .values()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Gemini returned an empty embedding."
                                )
                        );

        float[] vector =
                new float[values.size()];

        for (int index = 0;
                index < values.size();
                index++) {

            vector[index] =
                    values.get(index);
        }

        return vector;
    }

}