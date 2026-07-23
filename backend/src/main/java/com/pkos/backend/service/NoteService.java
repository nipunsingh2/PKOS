package com.pkos.backend.service;

import com.pkos.backend.dto.request.CreateNoteRequest;
import com.pkos.backend.dto.request.UpdateNoteRequest;
import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.NoteMapper;
import com.pkos.backend.repository.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.pkos.backend.dto.response.TagResponse;
import com.pkos.backend.entity.Tag;
import com.pkos.backend.mapper.TagMapper;
import com.pkos.backend.repository.TagRepository;

import java.util.List;


@Service
public class NoteService {

    private static final Logger logger =
        LoggerFactory.getLogger(NoteService.class);
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public NoteService(
                NoteRepository noteRepository,
                NoteMapper noteMapper,
                CurrentUserService currentUserService,
                AuditService auditService,
                TagRepository tagRepository,
                TagMapper tagMapper) {

        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        }

        @Transactional
        public NoteResponse createNote(CreateNoteRequest request) {

                User currentUser = currentUserService.getCurrentUser();

                logger.info("Creating note for user: {}", currentUser.getEmail());
                Note note = noteMapper.toEntity(request);
                note.setUser(currentUser);
                Note savedNote = noteRepository.save(note);
                auditService.logEvent(
                "Created Note",
                        currentUser.getEmail()
                );
                logger.info(
                        "Note created successfully. Note ID: {}, User: {}",
                        savedNote.getId(),
                        currentUser.getEmail());
                return noteMapper.toResponse(savedNote);
        }

        @Transactional(readOnly = true)
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



        @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Cacheable(value = "notes", key = "#id")
    public NoteResponse getNoteById(Long id) {
        User currentUser=currentUserService.getCurrentUser();
        Note note=noteRepository
                .findByIdAndUser(id,currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found"));
        return noteMapper.toResponse(note);
    }

    @Transactional
    @CacheEvict(value = "notes", key = "#id")
    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        User currentUser=currentUserService.getCurrentUser();
        Note note=noteRepository
                .findByIdAndUser(id,currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found" + id));
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setColor(request.getColor());
        Note updatedNote = noteRepository.save(note);
        auditService.logEvent(
        "Updated Note",
                currentUser.getEmail()
        );
        logger.info(
                "Note updated successfully. Note ID: {}, User: {}",
                updatedNote.getId(),
                currentUser.getEmail());
        return noteMapper.toResponse(updatedNote);
    }

        @Transactional
        @CacheEvict(value = "notes", key = "#id")
        public void deleteNote(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Note note = findOwnedNote(id, currentUser);

        for (Tag tag : note.getTags()) {
                tag.getNotes().remove(note);
        }

        note.getTags().clear();

        logger.info(
                "Note deleted successfully. Note ID: {}, User: {}",
                note.getId(),
                currentUser.getEmail()
        );

        auditService.logEvent(
                "Deleted Note",
                currentUser.getEmail()
        );

        noteRepository.delete(note);
        }

        @Transactional
        public NoteResponse addTagToNote(Long noteId, Long tagId) {

        User currentUser = currentUserService.getCurrentUser();

        Note note = findOwnedNote(noteId, currentUser);
        Tag tag = findOwnedTag(tagId, currentUser);

        if (note.getTags().contains(tag)) {
                return noteMapper.toResponse(note);
        }

        note.getTags().add(tag);
        tag.getNotes().add(note);

        Note updatedNote = noteRepository.save(note);

        auditService.logEvent(
                "Added Tag To Note",
                currentUser.getEmail()
        );

        logger.info(
                "Tag {} added to Note {} by {}",
                tag.getId(),
                note.getId(),
                currentUser.getEmail()
        );

        return noteMapper.toResponse(updatedNote);
        }


    @Transactional
        public void createNoteAndFail(CreateNoteRequest request) {

                User currentUser = currentUserService.getCurrentUser();
                Note note = noteMapper.toEntity(request);
                note.setUser(currentUser);
                noteRepository.save(note);
                auditService.logEvent(
                "Rollback Demo",
                        currentUser.getEmail());
                throw new RuntimeException(
                "Intentional exception to demonstrate transaction rollback."
                );
        }

        @Transactional
        public NoteResponse removeTagFromNote(Long noteId, Long tagId) {

        User currentUser = currentUserService.getCurrentUser();

        Note note = findOwnedNote(noteId, currentUser);
        Tag tag = findOwnedTag(tagId, currentUser);

        note.getTags().remove(tag);
        tag.getNotes().remove(note);

        Note updatedNote = noteRepository.save(note);

        auditService.logEvent(
                "Removed Tag From Note",
                currentUser.getEmail()
        );

        logger.info(
                "Tag {} removed from Note {} by {}",
                tag.getId(),
                note.getId(),
                currentUser.getEmail()
        );

        return noteMapper.toResponse(updatedNote);
        }

        @Transactional(readOnly = true)
        public List<TagResponse> getTagsOfNote(Long noteId) {

        User currentUser = currentUserService.getCurrentUser();

        Note note = findOwnedNote(noteId, currentUser);

        return note.getTags()
                .stream()
                .map(tagMapper::toResponse)
                .sorted((first, second) ->
                        first.getName().compareToIgnoreCase(second.getName()))
                .toList();
        }

        private Note findOwnedNote(Long noteId, User user) {

        return noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Note not found."));
        }

        private Tag findOwnedTag(Long tagId, User user) {

        return tagRepository.findByIdAndUser(tagId, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Tag not found."));
        }
}