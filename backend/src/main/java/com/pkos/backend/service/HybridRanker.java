package com.pkos.backend.service;

import org.springframework.stereotype.Component;

@Component
public class HybridRanker {

    private static final double SEMANTIC_WEIGHT = 0.70;

    private static final double KEYWORD_WEIGHT = 0.30;

    public double calculateScore(
            double semanticScore,
            double keywordScore
    ) {
        return (SEMANTIC_WEIGHT * semanticScore)
                + (KEYWORD_WEIGHT * keywordScore);
    }

}