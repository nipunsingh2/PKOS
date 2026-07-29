package com.pkos.backend.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.pkos.backend.entity.Note;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.service.NoteEmbeddingService;

@Component
public class NoteSavedEventListener {

    private static final Logger logger =
            LoggerFactory.getLogger(NoteSavedEventListener.class);

    private final NoteRepository noteRepository;
    private final NoteEmbeddingService noteEmbeddingService;

    public NoteSavedEventListener(
            NoteRepository noteRepository,
            NoteEmbeddingService noteEmbeddingService) {

        this.noteRepository = noteRepository;
        this.noteEmbeddingService = noteEmbeddingService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(NoteSavedEvent event) {

        logger.info(
                "Generating embedding for Note ID: {}",
                event.noteId()
        );

        Note note = noteRepository
                .findById(event.noteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found: " + event.noteId()
                        ));

        noteEmbeddingService.createOrUpdateEmbedding(note);

        logger.info(
                "Embedding generated successfully for Note ID: {}",
                note.getId()
        );
    }
}