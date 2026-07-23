package com.pkos.backend.controller;

import com.pkos.backend.dto.request.CreateNoteRequest;
import com.pkos.backend.dto.request.UpdateNoteRequest;
import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pkos.backend.dto.response.TagResponse;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @Valid @RequestBody CreateNoteRequest request) {

        return ResponseEntity.ok(noteService.createNote(request));
    }

    @GetMapping
    public ResponseEntity<Page<NoteResponse>> getNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(
                noteService.getNotes(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

        @GetMapping("/pinned")
        public ResponseEntity<List<NoteResponse>> getPinnedNotes() {

        return ResponseEntity.ok(
                noteService.getPinnedNotes()
        );
        }

    @GetMapping("/search")
    public ResponseEntity<Page<NoteResponse>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(
                noteService.searchNotes(
                        keyword,
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                noteService.getNoteById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request) {

        return ResponseEntity.ok(
                noteService.updateNote(id, request)
        );
    }

        @PutMapping("/{id}/pin")
        public ResponseEntity<NoteResponse> pinNote(
                @PathVariable Long id) {

        return ResponseEntity.ok(
                noteService.pinNote(id)
        );
        } 

        @PutMapping("/{id}/unpin")
        public ResponseEntity<NoteResponse> unpinNote(
                @PathVariable Long id) {

        return ResponseEntity.ok(
                noteService.unpinNote(id)
        );
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id) {

        noteService.deleteNote(id);

        return ResponseEntity.noContent().build();
    }

        @GetMapping("/trash")
        public ResponseEntity<Page<NoteResponse>> getTrashedNotes(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(defaultValue = "deletedAt") String sortBy,
                @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(
                noteService.getTrashedNotes(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
        }

        @PutMapping("/trash/{id}/restore")
        public ResponseEntity<NoteResponse> restoreNote(
                @PathVariable Long id) {

        return ResponseEntity.ok(
                noteService.restoreNote(id)
        );
        }

        @DeleteMapping("/trash/{id}")
        public ResponseEntity<Void> permanentlyDeleteNote(
                @PathVariable Long id) {

        noteService.permanentlyDeleteNote(id);

        return ResponseEntity.noContent().build();
        }


        @PostMapping("/{noteId}/tags/{tagId}")
        public ResponseEntity<NoteResponse> addTagToNote(
                @PathVariable Long noteId,
                @PathVariable Long tagId) {

        return ResponseEntity.ok(
                noteService.addTagToNote(noteId, tagId)
        );
        }

        @DeleteMapping("/{noteId}/tags/{tagId}")
        public ResponseEntity<NoteResponse> removeTagFromNote(
                @PathVariable Long noteId,
                @PathVariable Long tagId) {

        return ResponseEntity.ok(
                noteService.removeTagFromNote(noteId, tagId)
        );
        }

        @GetMapping("/{noteId}/tags")
        public ResponseEntity<List<TagResponse>> getTagsOfNote(
                @PathVariable Long noteId) {

        return ResponseEntity.ok(
                noteService.getTagsOfNote(noteId)
        );
        }

    @PostMapping("/rollback-demo")
    public ResponseEntity<Void> rollbackDemo(
            @Valid @RequestBody CreateNoteRequest request) {
        noteService.createNoteAndFail(request);
        return ResponseEntity.ok().build();
    }
}