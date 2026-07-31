package com.pkos.backend.dto.search;

import com.pkos.backend.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HybridSearchResult {

    private final Note note;

    private final double semanticScore;

    private final double keywordScore;

    private final double finalScore;

}