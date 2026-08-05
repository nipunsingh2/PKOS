package com.pkos.backend.service.search.ranking;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.config.SearchProperties;
import com.pkos.backend.service.search.model.SearchCandidate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HybridRankingServiceImpl implements HybridRankingService {

    private final SearchProperties searchProperties;

    @Override
    public List<SearchCandidate> rank(List<SearchCandidate> candidates) {

        candidates.forEach(this::calculateFinalScore);

        return candidates.stream()
                .sorted(
                        Comparator.comparingDouble(SearchCandidate::getFinalScore)
                                .reversed()
                )
                .toList();
    }

    private void calculateFinalScore(SearchCandidate candidate) {

        double finalScore =
                (candidate.getKeywordScore()
                        * searchProperties.getKeywordWeight())
                +
                (candidate.getSemanticScore()
                        * searchProperties.getSemanticWeight());

        candidate.setFinalScore(finalScore);
    }

}