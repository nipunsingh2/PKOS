package com.pkos.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pkos.backend.config.SearchProperties;
import com.pkos.backend.dto.search.HybridSearchResult;
import com.pkos.backend.dto.search.KeywordSearchResult;
import com.pkos.backend.dto.search.SemanticSearchResult;
import com.pkos.backend.entity.Note;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HybridSearchServiceImpl implements HybridSearchService {

    private final SemanticRetrievalService semanticRetrievalService;

    private final KeywordSearchService keywordSearchService;

    private final CurrentUserService currentUserService;

    private final HybridRanker hybridRanker;

    private final ScoreNormalizer scoreNormalizer;

    private final SearchProperties searchProperties;

    @Override
    public List<HybridSearchResult> search(String query) {

        List<SemanticSearchResult> semanticResults =
                semanticRetrievalService.retrieveRelevantNotes(query);

        List<KeywordSearchResult> keywordResults =
                keywordSearchService.search(
                        currentUserService.getCurrentUser(),
                        query,
                        searchProperties.getMaxResults()
                );

        List<Double> semanticScores = semanticResults.stream()
                .map(SemanticSearchResult::getSimilarity)
                .toList();

        List<Double> keywordScores = keywordResults.stream()
                .map(KeywordSearchResult::getKeywordRank)
                .toList();

        Map<Long, SemanticSearchResult> semanticMap =
                new HashMap<>();

        for (SemanticSearchResult result : semanticResults) {
            semanticMap.put(result.getNoteId(), result);
        }

        Map<Long, KeywordSearchResult> keywordMap =
                new HashMap<>();

        for (KeywordSearchResult result : keywordResults) {
            keywordMap.put(result.getNoteId(), result);
        }

        Map<Long, Note> notes = new HashMap<>();

        semanticResults.forEach(result ->
                notes.put(
                        result.getNoteId(),
                        result.getNote()
                )
        );

        keywordResults.forEach(result ->
                notes.put(
                        result.getNoteId(),
                        result.getNote()
                )
        );

        List<HybridSearchResult> hybridResults =
                new ArrayList<>();

        for (Long noteId : notes.keySet()) {

            SemanticSearchResult semantic =
                    semanticMap.get(noteId);

            KeywordSearchResult keyword =
                    keywordMap.get(noteId);

            double semanticScore =
                    semantic != null
                            ? scoreNormalizer.normalize(
                                    semantic.getSimilarity(),
                                    semanticScores
                            )
                            : 0.0;

            double keywordScore =
                    keyword != null
                            ? scoreNormalizer.normalize(
                                    keyword.getKeywordRank(),
                                    keywordScores
                            )
                            : 0.0;

            double finalScore =
                    hybridRanker.calculateScore(
                            semanticScore,
                            keywordScore
                    );

            hybridResults.add(
                    new HybridSearchResult(
                            notes.get(noteId),
                            semanticScore,
                            keywordScore,
                            finalScore
                    )
            );
        }

        return hybridResults.stream()
                .sorted(
                        Comparator.comparingDouble(
                                HybridSearchResult::getFinalScore
                        ).reversed()
                )
                .limit(searchProperties.getMaxResults())
                .toList();
    }
}