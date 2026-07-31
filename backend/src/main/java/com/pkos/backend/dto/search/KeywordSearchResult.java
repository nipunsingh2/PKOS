package com.pkos.backend.dto.search;

import com.pkos.backend.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordSearchResult {

    private final Note note;

    private final double keywordRank;

    public Long getNoteId() {
    return note.getId();
}
}