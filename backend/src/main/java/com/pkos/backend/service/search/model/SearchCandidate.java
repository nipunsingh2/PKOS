package com.pkos.backend.service.search.model;

import java.util.Map;
import com.pkos.backend.entity.SearchResultType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCandidate {

    private Long id;
    
    private SearchSourceType sourceType;

    private SearchResultType resultType;

    private String title;

    private String snippet;

    private double keywordScore;

    private double semanticScore;

    private double finalScore;

    private Map<String, Object> metadata;
}