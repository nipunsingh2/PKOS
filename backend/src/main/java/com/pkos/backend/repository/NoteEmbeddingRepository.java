package com.pkos.backend.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.NoteEmbedding;

public interface NoteEmbeddingRepository extends JpaRepository<NoteEmbedding, Long> {

    Optional<NoteEmbedding> findByNote(Note note);

    boolean existsByNote(Note note);

    void deleteByNote(Note note);

    @Query(value = """
        SELECT ne.*
        FROM note_embeddings ne
        JOIN notes n ON n.id = ne.note_id
        WHERE n.user_id = :userId
        AND n.deleted = false
        AND n.archived = false
        ORDER BY ne.embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
        """,
        nativeQuery = true)
    List<NoteEmbedding> findMostSimilarNotes(
            @Param("userId") Long userId,
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );

}