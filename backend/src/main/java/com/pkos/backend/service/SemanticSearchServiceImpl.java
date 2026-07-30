package com.pkos.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.SearchMapper;
import com.pkos.backend.repository.NoteEmbeddingRepository;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.repository.projection.SemanticSearchProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final EmbeddingService embeddingService;

    private final NoteEmbeddingRepository noteEmbeddingRepository;

    private final NoteRepository noteRepository;

    private final CurrentUserService currentUserService;

    private final SearchMapper searchMapper;

    @Value("${pkos.search.semantic.threshold}")
    private double similarityThreshold;

    @Value("${pkos.search.semantic.limit}")
    private int searchLimit;

    @Override
    public SearchResponse search(String query) {

        User currentUser = currentUserService.getCurrentUser();

        float[] embedding = embeddingService.generateEmbedding(query);

        String pgVector = toPgVector(embedding);

        List<SemanticSearchProjection> matches =
                noteEmbeddingRepository.findMostSimilarNotesWithScore(
                        currentUser.getId(),
                        pgVector,
                        searchLimit
                );

        List<SearchResult> results = matches.stream()
                .filter(match -> match.getSimilarity() >= similarityThreshold)
                .map(match -> noteRepository.findById(match.getNoteId())
                        .orElseThrow())
                .map(searchMapper::fromNote)
                .toList();

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }

    private String toPgVector(float[] embedding) {

        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < embedding.length; i++) {

            builder.append(embedding[i]);

            if (i < embedding.length - 1) {
                builder.append(",");
            }
        }

        builder.append("]");

        return builder.toString();
    }

}