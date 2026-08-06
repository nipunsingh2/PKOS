package com.pkos.backend.service.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import com.pkos.backend.dto.search.SemanticSearchResult;
import com.pkos.backend.service.SemanticRetrievalService;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.NoteEmbedding;
import com.pkos.backend.entity.enums.KnowledgeNodeType;
import com.pkos.backend.entity.enums.RelationshipType;
import com.pkos.backend.repository.NoteEmbeddingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelationshipDiscoveryServiceImpl
        implements RelationshipDiscoveryService {

    private static final Logger logger =
            LoggerFactory.getLogger(RelationshipDiscoveryServiceImpl.class);

    private final NoteEmbeddingRepository noteEmbeddingRepository;

    private final KnowledgeGraphService knowledgeGraphService;

    private final SemanticRetrievalService semanticRetrievalService;

    @Value("${pkos.search.semantic.threshold}")
    private double similarityThreshold;

    @Override
    public void discoverRelationships(Note note) {

        NoteEmbedding embedding = noteEmbeddingRepository
                .findByNote(note)
                .orElse(null);

        if (embedding == null) {

            logger.warn(
                    "No embedding found for Note ID: {}",
                    note.getId()
            );

            return;
        }

        logger.info(
                "Embedding found for Note ID: {}",
                note.getId()
        );

        List<SemanticSearchResult> similarNotes =
                semanticRetrievalService.retrieveRelevantNotes(
                        note.getTitle() + "\n\n" + note.getContent()
                );

        logger.info(
                "Found {} semantic candidates for Note ID: {}",
                similarNotes.size(),
                note.getId()
        );

        for (SemanticSearchResult result : similarNotes) {

            if (result.getNoteId().equals(note.getId())) {

                logger.debug(
                        "Skipping current note: {}",
                        note.getId()
                );

                continue;
            }

        logger.info(
                "Candidate -> Note ID: {}, Similarity: {}",
                result.getNoteId(),
                result.getSimilarity()
        );

        if (!knowledgeGraphService.relationshipExists(
                KnowledgeNodeType.NOTE,
                note.getId(),
                KnowledgeNodeType.NOTE,
                result.getNoteId(),
                RelationshipType.RELATED)) {

            knowledgeGraphService.createRelationship(
                    KnowledgeNodeType.NOTE,
                    note.getId(),
                    KnowledgeNodeType.NOTE,
                    result.getNoteId(),
                    RelationshipType.RELATED,
                    result.getSimilarity());

            logger.info(
                    "Created RELATED relationship: {} -> {}",
                    note.getId(),
                    result.getNoteId());
        }
        else {

            logger.debug(
                    "Relationship already exists: {} -> {}",
                    note.getId(),
                    result.getNoteId());
        }
        }

    }

}