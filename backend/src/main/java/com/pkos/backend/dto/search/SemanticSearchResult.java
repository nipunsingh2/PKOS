package com.pkos.backend.dto.search;

import com.pkos.backend.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SemanticSearchResult {

    private final Note note;

    private final double similarity;
    public Long getNoteId() {
    return note.getId();
}
}