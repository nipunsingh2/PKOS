package com.pkos.backend.service;

import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.mapper.NoteMapper;
import com.pkos.backend.dto.request.CreateNotebookRequest;
import com.pkos.backend.dto.response.NotebookResponse;
import com.pkos.backend.entity.Notebook;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.DuplicateResourceException;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.NotebookMapper;
import com.pkos.backend.repository.NotebookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pkos.backend.dto.request.UpdateNotebookRequest;
import com.pkos.backend.entity.Note;
import com.pkos.backend.repository.NoteRepository;
import java.util.List;
import com.pkos.backend.util.AppConstants;

@Service
public class NotebookService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotebookService.class);

    private final NotebookRepository notebookRepository;
    private final NotebookMapper notebookMapper;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NotebookService(
            NotebookRepository notebookRepository,
            NoteRepository noteRepository,
            NotebookMapper notebookMapper,
            CurrentUserService currentUserService,
            AuditService auditService,
            NoteMapper noteMapper) {

        this.notebookRepository = notebookRepository;
        this.noteRepository = noteRepository;
        this.notebookMapper = notebookMapper;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.noteMapper = noteMapper;
    }

    @Transactional
    public NotebookResponse createNotebook(
            CreateNotebookRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        String normalizedName =
                normalizeNotebookName(request.getName());

        if (notebookRepository.existsByNameIgnoreCaseAndUser(
                normalizedName,
                currentUser)) {

            throw new DuplicateResourceException(
                    "Notebook already exists."
            );
        }

        request.setName(normalizedName);

        Notebook notebook =
                notebookMapper.toEntity(request);

        notebook.setUser(currentUser);

        Notebook savedNotebook =
                notebookRepository.save(notebook);

        auditService.logEvent(
                "Created Notebook",
                currentUser.getEmail()
        );

        logger.info(
                "Notebook created successfully. Notebook ID: {}, User: {}",
                savedNotebook.getId(),
                currentUser.getEmail()
        );

        return notebookMapper.toResponse(savedNotebook);
    }

    @Transactional(readOnly = true)
    public Page<NotebookResponse> getNotebooks(
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser =
                currentUserService.getCurrentUser();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        return notebookRepository
                .findAllByUser(currentUser, pageable)
                .map(notebookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public NotebookResponse getNotebookById(Long id) {

        User currentUser =
                currentUserService.getCurrentUser();

        Notebook notebook =
                findOwnedNotebook(id, currentUser);

        return notebookMapper.toResponse(notebook);
    }

    @Transactional
    public NotebookResponse updateNotebook(
            Long id,
            UpdateNotebookRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Notebook notebook = findOwnedNotebook(id, currentUser);

        if (AppConstants.DEFAULT_NOTEBOOK_NAME.equals(notebook.getName())) {
            throw new IllegalArgumentException(
                    "Default Inbox notebook cannot be renamed."
            );
        }

        String normalizedName =
                normalizeNotebookName(request.getName());

        if (notebookRepository.existsByNameIgnoreCaseAndUserAndIdNot(
                normalizedName,
                currentUser,
                id)) {

            throw new DuplicateResourceException(
                    "Notebook already exists."
            );
        }

        request.setName(normalizedName);

        notebookMapper.updateEntity(notebook, request);

        Notebook updatedNotebook =
                notebookRepository.save(notebook);

        auditService.logEvent(
                "Updated Notebook",
                currentUser.getEmail()
        );

        logger.info(
                "Notebook updated successfully. Notebook ID: {}, User: {}",
                updatedNotebook.getId(),
                currentUser.getEmail()
        );

        return notebookMapper.toResponse(updatedNotebook);
    }

    @Transactional
    public void deleteNotebook(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Notebook notebook =
                findOwnedNotebook(id, currentUser);

        if (AppConstants.DEFAULT_NOTEBOOK_NAME.equals(notebook.getName())) {
            throw new IllegalArgumentException(
                    "Default Inbox notebook cannot be deleted."
            );
        }

        Notebook inbox =
                findInboxNotebook(currentUser);

        List<Note> notes =
                noteRepository.findAllByNotebookAndUserAndDeletedFalseAndArchivedFalse(

                        notebook,
                        currentUser
                );

        for (Note note : notes) {
            note.setNotebook(inbox);
        }

        notebookRepository.delete(notebook);

        auditService.logEvent(
                "Deleted Notebook",
                currentUser.getEmail()
        );

        logger.info(
                "Notebook deleted successfully. Notebook ID: {}, User: {}",
                notebook.getId(),
                currentUser.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public Page<NoteResponse> getNotebookNotes(
            Long notebookId,
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser =
                currentUserService.getCurrentUser();

        Notebook notebook =
                findOwnedNotebook(notebookId, currentUser);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        return noteRepository
                .findByNotebookAndUserAndDeletedFalseAndArchivedFalse(
                        notebook,
                        currentUser,
                        pageable
                )
                .map(noteMapper::toResponse);
    }

    @Transactional
    public NoteResponse moveNoteToNotebook(
            Long noteId,
            Long notebookId) {

        User currentUser =
                currentUserService.getCurrentUser();

        Note note = noteRepository
                .findByIdAndUserAndDeletedFalseAndArchivedFalse(noteId, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Note not found."
                        ));

        Notebook notebook =
                findOwnedNotebook(notebookId, currentUser);

        note.setNotebook(notebook);

        Note updatedNote =
                noteRepository.save(note);

        auditService.logEvent(
                "Moved Note To Notebook",
                currentUser.getEmail()
        );

        logger.info(
                "Note {} moved to Notebook {} by {}",
                updatedNote.getId(),
                notebook.getId(),
                currentUser.getEmail()
        );

        return noteMapper.toResponse(updatedNote);
    }

    private Notebook findOwnedNotebook(
            Long id,
            User user) {

        return notebookRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notebook not found."
                        ));
    }

    private Notebook findInboxNotebook(User user) {

        return notebookRepository
                .findByUserAndName(
                        user,
                        AppConstants.DEFAULT_NOTEBOOK_NAME
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default Inbox notebook not found."
                        ));
    }

    private String normalizeNotebookName(String name) {
        return name.trim();
    }
}