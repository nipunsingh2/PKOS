package com.pkos.backend.mapper;

import com.pkos.backend.dto.request.CreateNoteRequest;
import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.entity.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    public Note toEntity(CreateNoteRequest request) {

        Note note = new Note();

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        return note;
    }

    public NoteResponse toResponse(Note note) {

        NoteResponse response = new NoteResponse();

        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());

        return response;
    }
}