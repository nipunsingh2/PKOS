package com.pkos.backend.controller;

import com.pkos.backend.dto.request.CreateNotebookRequest;
import com.pkos.backend.dto.request.UpdateNotebookRequest;
import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.dto.response.NotebookResponse;
import com.pkos.backend.service.NotebookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notebooks")
@RequiredArgsConstructor
public class NotebookController {

    private final NotebookService notebookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotebookResponse createNotebook(
            @Valid @RequestBody CreateNotebookRequest request
    ) {
        return notebookService.createNotebook(request);
    }

    @GetMapping
    public Page<NotebookResponse> getNotebooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return notebookService.getNotebooks(
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{id}")
    public NotebookResponse getNotebook(
            @PathVariable Long id
    ) {
        return notebookService.getNotebookById(id);
    }

    @PutMapping("/{id}")
    public NotebookResponse updateNotebook(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNotebookRequest request
    ) {
        return notebookService.updateNotebook(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotebook(
            @PathVariable Long id
    ) {
        notebookService.deleteNotebook(id);
    }

    @GetMapping("/{id}/notes")
    public Page<NoteResponse> getNotebookNotes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return notebookService.getNotebookNotes(
                id,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PutMapping("/{notebookId}/notes/{noteId}")
    public NoteResponse moveNoteToNotebook(
            @PathVariable Long notebookId,
            @PathVariable Long noteId
    ) {
        return notebookService.moveNoteToNotebook(
                noteId,
                notebookId
        );
    }
}