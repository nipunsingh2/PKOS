package com.pkos.backend.mapper;

import com.pkos.backend.dto.request.CreateNoteRequest;
import com.pkos.backend.dto.response.NoteResponse;
import com.pkos.backend.dto.response.TagResponse;
import com.pkos.backend.entity.Note;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NoteMapper {

    private final TagMapper tagMapper;

    public NoteMapper(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    public Note toEntity(CreateNoteRequest request) {

        Note note = new Note();

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setColor(request.getColor());

        return note;
    }

    public NoteResponse toResponse(Note note) {

    NoteResponse response = new NoteResponse();

    response.setId(note.getId());
    response.setTitle(note.getTitle());
    response.setContent(note.getContent());
    response.setCreatedAt(note.getCreatedAt());
    response.setUpdatedAt(note.getUpdatedAt());
    response.setColor(note.getColor());

    Set<TagResponse> tags = note.getTags() == null
        ? Set.of()
        : note.getTags()
                .stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toSet());

    response.setTags(tags);

    return response;
}
}