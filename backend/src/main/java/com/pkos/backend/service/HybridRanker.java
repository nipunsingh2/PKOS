package com.pkos.backend.service;

import org.springframework.stereotype.Component;

import com.pkos.backend.config.SearchProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HybridRanker {

    private final SearchProperties searchProperties;

    public double calculateScore(
            double semanticScore,
            double keywordScore
    ) {

        return (searchProperties.getSemanticWeight()
                * semanticScore)
                + (searchProperties.getKeywordWeight()
                * keywordScore);
    }
}