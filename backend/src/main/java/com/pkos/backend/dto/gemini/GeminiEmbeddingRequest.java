package com.pkos.backend.dto.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeminiEmbeddingRequest {

    private String model;

    private GeminiContent content;

    @JsonProperty("outputDimensionality")
    private Integer outputDimensionality;

}