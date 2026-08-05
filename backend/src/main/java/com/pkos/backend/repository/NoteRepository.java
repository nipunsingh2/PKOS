package com.pkos.backend.repository;

import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.Notebook;
import com.pkos.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pkos.backend.repository.projection.KeywordSearchProjection;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByUserAndDeletedFalseAndArchivedFalse(
            User user,
            Pageable pageable
    );

    Optional<Note> findByIdAndUserAndDeletedFalse(
        Long id,
        User user
        );
        
    Optional<Note> findByIdAndUserAndDeletedFalseAndArchivedFalse(
            Long id,
            User user
    );

    List<Note> findAllByIdIn(List<Long> ids);
    
    Page<Note> findByNotebookAndUserAndDeletedFalseAndArchivedFalse(
            Notebook notebook,
            User user,
            Pageable pageable
    );

    List<Note> findAllByNotebookAndUserAndDeletedFalseAndArchivedFalse(
            Notebook notebook,
            User user
    );


    List<Note> findByUserAndDeletedFalseAndPinnedTrue(
            User user
    );

    Page<Note> findByUserAndDeletedFalseAndArchivedTrue(
            User user,
            Pageable pageable
    );

    Optional<Note> findByIdAndUserAndDeletedFalseAndArchivedTrue(
            Long id,
            User user
    );

    Page<Note> findByUserAndDeletedTrue(
            User user,
            Pageable pageable
    );

    Optional<Note> findByIdAndUserAndDeletedTrue(
            Long id,
            User user
    );


        @Query(value = """
                SELECT *
                FROM notes n
                WHERE n.user_id = :userId
                AND n.deleted = false
                AND n.archived = false
                AND n.search_vector @@ websearch_to_tsquery('simple', :query)
                """,
                countQuery = """
                SELECT COUNT(*)
                FROM notes n
                WHERE n.user_id = :userId
                AND n.deleted = false
                AND n.archived = false
                AND n.search_vector @@ websearch_to_tsquery('simple', :query)
                """,
                nativeQuery = true)
        Page<Note> searchUserNotes(
                @Param("userId") Long userId,
                @Param("query") String query,
                Pageable pageable
        );


        @Query(value = """
        SELECT
        n.id AS noteId,
        n.title AS title,
        n.content AS content,
        ts_rank_cd(
                n.search_vector,
                websearch_to_tsquery('simple', :query)
        ) AS keywordRank
        FROM notes n
        WHERE n.user_id = :userId
        AND n.deleted = false
        AND n.archived = false
        AND n.search_vector @@ websearch_to_tsquery('simple', :query)
        ORDER BY keywordRank DESC
        LIMIT :limit
        """, nativeQuery = true)
        List<KeywordSearchProjection> keywordSearch(
                @Param("userId") Long userId,
                @Param("query") String query,
                @Param("limit") int limit
        );

}