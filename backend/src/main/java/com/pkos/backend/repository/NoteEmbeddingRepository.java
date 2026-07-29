package com.pkos.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.NoteEmbedding;

public interface NoteEmbeddingRepository extends JpaRepository<NoteEmbedding, Long> {

    Optional<NoteEmbedding> findByNote(Note note);

    boolean existsByNote(Note note);

    void deleteByNote(Note note);

}