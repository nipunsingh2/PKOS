package com.pkos.backend.repository;

import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.Reminder;
import com.pkos.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Optional<Reminder> findByNote(Note note);

    Optional<Reminder> findByIdAndNoteUser(
            Long id,
            User user
    );

    List<Reminder> findByNoteUserAndCompletedFalseOrderByRemindAtAsc(
            User user
    );

    List<Reminder> findByCompletedFalseAndRemindAtLessThanEqual(
            LocalDateTime dateTime
    );

    List<Reminder> findByNoteUserAndCompletedFalseAndRemindAtBeforeOrderByRemindAtAsc(
            User user,
            LocalDateTime dateTime
    );

    List<Reminder> findByNoteUserAndCompletedFalseAndRemindAtAfterOrderByRemindAtAsc(
            User user,
            LocalDateTime dateTime
    );
}