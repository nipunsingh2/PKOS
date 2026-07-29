package com.pkos.backend.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.config.GeminiProperties;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.NoteEmbedding;
import com.pkos.backend.repository.NoteEmbeddingRepository;

@Service
@Transactional
public class NoteEmbeddingServiceImpl implements NoteEmbeddingService {

    private final EmbeddingService embeddingService;
    private final NoteEmbeddingRepository noteEmbeddingRepository;
    private final GeminiProperties geminiProperties;

    public NoteEmbeddingServiceImpl(
            EmbeddingService embeddingService,
            NoteEmbeddingRepository noteEmbeddingRepository,
            GeminiProperties geminiProperties) {

        this.embeddingService = embeddingService;
        this.noteEmbeddingRepository = noteEmbeddingRepository;
        this.geminiProperties = geminiProperties;
    }

    @Override
    public void createOrUpdateEmbedding(Note note) {

        String embeddingText = buildEmbeddingText(note);

        float[] vector = embeddingService.generateEmbedding(embeddingText);

        NoteEmbedding noteEmbedding = noteEmbeddingRepository
                .findByNote(note)
                .orElseGet(NoteEmbedding::new);

        noteEmbedding.setNote(note);
        noteEmbedding.setEmbedding(vector);
        noteEmbedding.setEmbeddingModel(geminiProperties.getEmbeddingModel());
        noteEmbedding.setEmbeddingVersion(geminiProperties.getEmbeddingVersion());

        noteEmbeddingRepository.save(noteEmbedding);
    }

    @Override
    public void deleteEmbedding(Note note) {

        noteEmbeddingRepository.deleteByNote(note);
    }

    private String buildEmbeddingText(Note note) {

        String notebookName = note.getNotebook() != null
                ? note.getNotebook().getName()
                : "";

        String tags = note.getTags()
                .stream()
                .map(tag -> tag.getName())
                .collect(Collectors.joining(", "));

        return String.format("""
                Title: %s

                Notebook: %s

                Tags: %s

                Content:
                %s
                """,
                note.getTitle(),
                notebookName,
                tags,
                note.getContent());
    }
}