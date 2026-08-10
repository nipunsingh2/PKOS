package com.pkos.backend.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.pkos.backend.dto.search.SemanticSearchResult;
import com.pkos.backend.entity.User;
import com.pkos.backend.repository.NoteEmbeddingRepository;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.repository.projection.SemanticSearchProjection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemanticRetrievalServiceImpl
        implements SemanticRetrievalService {

    private static final Logger logger =
            LoggerFactory.getLogger(SemanticRetrievalServiceImpl.class);

    private final EmbeddingService embeddingService;

    private final NoteEmbeddingRepository noteEmbeddingRepository;

    private final NoteRepository noteRepository;

    private final CurrentUserService currentUserService;

    @Value("${pkos.search.semantic.threshold}")
    private double similarityThreshold;

    @Value("${pkos.search.semantic.limit}")
    private int searchLimit;

    @Override
    public List<SemanticSearchResult> retrieveRelevantNotes(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Query cannot be null or blank.");
        }

        User currentUser = currentUserService.getCurrentUser();

        logger.debug("Semantic query: {}", query);

        float[] embedding = embeddingService.generateEmbedding(query);

        logger.debug(
                "Embedding generated. Dimension = {}",
                embedding.length);

        String embeddingVector = toPgVector(embedding);

        List<SemanticSearchProjection> matches =
                noteEmbeddingRepository.findMostSimilarNotesWithScore(
                        currentUser.getId(),
                        embeddingVector,
                        searchLimit);

        logger.debug(
                "Vector search returned {} matches",
                matches.size());

        List<SemanticSearchResult> results = new ArrayList<>();

        for (SemanticSearchProjection match : matches) {

            logger.debug(
                    "Match -> NoteId={}, Similarity={}",
                    match.getNoteId(),
                    match.getSimilarity());

            if (match.getSimilarity() < similarityThreshold) {

                logger.debug(
                        "Rejected because similarity is below threshold");

                continue;
            }

                noteRepository
                        .findByIdAndUserAndDeletedFalseAndArchivedFalse(
                                match.getNoteId(),
                                currentUser)
                        .ifPresent(note -> {

                        logger.debug(
                                "Accepted note: {}",
                                note.getTitle());

                        results.add(
                                new SemanticSearchResult(
                                        note,
                                        match.getSimilarity()
                                )
                        );
                        });
        }

        logger.debug(
                "Final retrieved notes = {}",
                results.size());

        return results;
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