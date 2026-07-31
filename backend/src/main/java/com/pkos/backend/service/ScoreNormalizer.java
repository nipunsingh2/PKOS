package com.pkos.backend.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.stereotype.Component;

@Component
public class ScoreNormalizer {

    public double normalize(
            double score,
            Collection<Double> allScores
    ) {

        if (allScores.isEmpty()) {
            return 0.0;
        }

        double min = Collections.min(allScores);
        double max = Collections.max(allScores);

        if (Double.compare(min, max) == 0) {
            return 1.0;
        }

        return (score - min) / (max - min);
    }

}