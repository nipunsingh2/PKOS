package com.pkos.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pkos.backend.entity.NoteEmbedding;
import com.pkos.backend.entity.User;
import com.pkos.backend.repository.NoteEmbeddingRepository;
import java.util.List;

import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.mapper.NoteMapper;
import com.pkos.backend.mapper.SearchMapper;


@Service
@Transactional(readOnly = true)
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private static final int DEFAULT_LIMIT = 10;

    private final EmbeddingService embeddingService;
    private final NoteEmbeddingRepository noteEmbeddingRepository;
    private final CurrentUserService currentUserService;
    private final SearchMapper searchMapper;

    public SemanticSearchServiceImpl(
            EmbeddingService embeddingService,
            NoteEmbeddingRepository noteEmbeddingRepository,
            CurrentUserService currentUserService,
            SearchMapper searchMapper) {

        this.embeddingService = embeddingService;
        this.noteEmbeddingRepository = noteEmbeddingRepository;
        this.currentUserService = currentUserService;
        this.searchMapper = searchMapper;
    }

    @Override
    public SearchResponse search(String query) {

        User currentUser = currentUserService.getCurrentUser();

        float[] embedding = embeddingService.generateEmbedding(query);

        String pgVector = toPgVector(embedding);

        List<NoteEmbedding> embeddings =
                noteEmbeddingRepository.findMostSimilarNotes(
                        currentUser.getId(),
                        pgVector,
                        DEFAULT_LIMIT
                );

        List<SearchResult> results = embeddings.stream()
                .map(NoteEmbedding::getNote)
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