package com.pkos.backend.dto.gemini;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeminiEmbeddingResponse {

    private Embedding embedding;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Embedding {

        private float[] values;

    }

}