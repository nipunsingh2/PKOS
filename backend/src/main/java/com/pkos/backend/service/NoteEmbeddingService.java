package com.pkos.backend.service;

import com.pkos.backend.entity.Note;

public interface NoteEmbeddingService {

    void createOrUpdateEmbedding(Note note);

    void deleteEmbedding(Note note);

}