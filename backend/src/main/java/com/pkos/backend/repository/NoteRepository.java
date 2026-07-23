package com.pkos.backend.repository;

import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.Notebook;
import com.pkos.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findByUserAndDeletedFalse(
            User user,
            Pageable pageable
    );

    Optional<Note> findByIdAndUserAndDeletedFalse(
            Long id,
            User user
    );

    Page<Note> findByNotebookAndUserAndDeletedFalse(
            Notebook notebook,
            User user,
            Pageable pageable
    );

    List<Note> findAllByNotebookAndUserAndDeletedFalse(
            Notebook notebook,
            User user
    );

    @Query("""
        SELECT n
        FROM Note n
        WHERE n.user = :user
        AND n.deleted = false
        AND (
            LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        """)
    Page<Note> searchUserNotes(
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    List<Note> findByUserAndDeletedFalseAndPinnedTrue(User user);

    Page<Note> findByUserAndDeletedTrue(
            User user,
            Pageable pageable
    );

    Optional<Note> findByIdAndUserAndDeletedTrue(
            Long id,
            User user
    );

}