package com.pkos.backend.service;

import com.pkos.backend.exception.ReminderAlreadyExistsException;
import com.pkos.backend.dto.request.CreateReminderRequest;
import com.pkos.backend.dto.request.UpdateReminderRequest;
import com.pkos.backend.dto.response.ReminderResponse;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.Reminder;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.ReminderMapper;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.repository.ReminderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {

    private static final Logger logger =
            LoggerFactory.getLogger(ReminderService.class);

    private final ReminderRepository reminderRepository;
    private final ReminderMapper reminderMapper;
    private final NoteRepository noteRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public ReminderService(
            ReminderRepository reminderRepository,
            ReminderMapper reminderMapper,
            NoteRepository noteRepository,
            CurrentUserService currentUserService,
            AuditService auditService) {

        this.reminderRepository = reminderRepository;
        this.reminderMapper = reminderMapper;
        this.noteRepository = noteRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @Transactional
    public ReminderResponse createReminder(
            Long noteId,
            CreateReminderRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Note note = noteRepository
                .findByIdAndUserAndDeletedFalseAndArchivedFalse(
                        noteId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found."));

                reminderRepository.findByNote(note)
                        .ifPresent(reminder -> {
                        throw new ReminderAlreadyExistsException(
                                "This note already has a reminder."
                        );
                        });

        Reminder reminder = new Reminder();

        reminder.setNote(note);
        reminder.setRemindAt(request.remindAt());

        Reminder savedReminder =
                reminderRepository.save(reminder);

        auditService.logEvent(
                "Created Reminder",
                currentUser.getEmail()
        );

        logger.info(
                "Reminder created successfully. Reminder ID: {}, Note ID: {}, User: {}",
                savedReminder.getId(),
                note.getId(),
                currentUser.getEmail()
        );

        return reminderMapper.toResponse(savedReminder);
    }

    @Transactional
    public ReminderResponse updateReminder(
            Long reminderId,
            UpdateReminderRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Reminder reminder = reminderRepository
                .findByIdAndNoteUser(
                        reminderId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reminder not found."));

        reminder.setRemindAt(request.remindAt());

        Reminder updatedReminder =
                reminderRepository.save(reminder);

        auditService.logEvent(
                "Updated Reminder",
                currentUser.getEmail()
        );

        logger.info(
                "Reminder updated successfully. Reminder ID: {}, User: {}",
                updatedReminder.getId(),
                currentUser.getEmail()
        );

        return reminderMapper.toResponse(updatedReminder);
    }

    @Transactional
    public void deleteReminder(Long reminderId) {

        User currentUser = currentUserService.getCurrentUser();

        Reminder reminder = reminderRepository
                .findByIdAndNoteUser(
                        reminderId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reminder not found."));

        reminderRepository.delete(reminder);

        auditService.logEvent(
                "Deleted Reminder",
                currentUser.getEmail()
        );

        logger.info(
                "Reminder deleted successfully. Reminder ID: {}, User: {}",
                reminderId,
                currentUser.getEmail()
        );
    }

    @Transactional
    public ReminderResponse markCompleted(Long reminderId) {

        User currentUser = currentUserService.getCurrentUser();

        Reminder reminder = reminderRepository
                .findByIdAndNoteUser(
                        reminderId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reminder not found."));

        reminder.setCompleted(true);

        Reminder updatedReminder =
                reminderRepository.save(reminder);

        auditService.logEvent(
                "Completed Reminder",
                currentUser.getEmail()
        );

        logger.info(
                "Reminder completed. Reminder ID: {}, User: {}",
                reminderId,
                currentUser.getEmail()
        );

        return reminderMapper.toResponse(updatedReminder);
    }

    @Transactional
    public List<ReminderResponse> getPendingReminders() {

        User currentUser = currentUserService.getCurrentUser();

        return reminderRepository
                .findByNoteUserAndCompletedFalseOrderByRemindAtAsc(currentUser)
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<ReminderResponse> getUpcomingReminders() {

        User currentUser = currentUserService.getCurrentUser();

        return reminderRepository
                .findByNoteUserAndCompletedFalseAndRemindAtAfterOrderByRemindAtAsc(
                        currentUser,
                        LocalDateTime.now()
                )
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

    @Transactional
    public List<ReminderResponse> getOverdueReminders() {

        User currentUser = currentUserService.getCurrentUser();

        return reminderRepository
                .findByNoteUserAndCompletedFalseAndRemindAtBeforeOrderByRemindAtAsc(
                        currentUser,
                        LocalDateTime.now()
                )
                .stream()
                .map(reminderMapper::toResponse)
                .toList();
    }

}