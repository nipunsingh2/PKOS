package com.pkos.backend.service;

import com.pkos.backend.dto.request.CreateNoteRequest;
import com.pkos.backend.dto.request.UpdateNoteRequest;
import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.NoteMapper;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NoteService {

    private static final Logger logger =
        LoggerFactory.getLogger(NoteService.class);
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final CurrentUserService currentUserService;

    public NoteService(
                NoteRepository noteRepository,
                NoteMapper noteMapper,
                CurrentUserService currentUserService) {

        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.currentUserService = currentUserService;
        }
        
    public NoteResponse createNote(CreateNoteRequest request) {

        User currentUser = currentUserService.getCurrentUser();
        logger.info("Creating note for user: {}", currentUser.getEmail());
        Note note = noteMapper.toEntity(request);
        note.setUser(currentUser);
        Note savedNote = noteRepository.save(note);
        logger.info(
                "Note created successfully. Note ID: {}, User: {}",
                savedNote.getId(),
                currentUser.getEmail());
        return noteMapper.toResponse(savedNote);
        }

    public Page<NoteResponse> getNotes(
                int page,
                int size,
                String sortBy,
                String direction) {

        User currentUser = currentUserService.getCurrentUser();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return noteRepository
                .findByUser(currentUser, pageable)
                .map(noteMapper::toResponse);
        }



    public Page<NoteResponse> searchNotes(
                String keyword,
                int page,
                int size,
                String sortBy,
                String direction) {
        User currentUser = currentUserService.getCurrentUser();


        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return noteRepository
                .searchUserNotes(
                        currentUser,
                        keyword,
                        pageable
                )
                .map(noteMapper::toResponse);
    }

    public NoteResponse getNoteById(Long id) {
        User currentUser=currentUserService.getCurrentUser();
        Note note=noteRepository
                .findByIdAndUser(id,currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found"));
        return noteMapper.toResponse(note);
    }

    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        User currentUser=currentUserService.getCurrentUser();
        Note note=noteRepository
                .findByIdAndUser(id,currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found" + id));
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        Note updatedNote = noteRepository.save(note);
        logger.info(
                "Note updated successfully. Note ID: {}, User: {}",
                updatedNote.getId(),
                currentUser.getEmail());
        return noteMapper.toResponse(updatedNote);
    }

    public void deleteNote(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        Note note = noteRepository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found" + id));
        logger.info(
                "Note deleted successfully. Note ID: {}, User: {}",
                note.getId(),
                currentUser.getEmail());
        noteRepository.delete(note);
    }
}