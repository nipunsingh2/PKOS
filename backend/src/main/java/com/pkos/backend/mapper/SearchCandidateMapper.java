package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.entity.FileContent;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.SearchResultType;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.model.SearchSourceType;

@Component
public class SearchCandidateMapper {

    public SearchCandidate fromNote(Note note) {
        return SearchCandidate.builder()
                .id(note.getId())
                .sourceType(SearchSourceType.NOTE)
                .resultType(SearchResultType.NOTE)
                .title(note.getTitle())
                .snippet(createSnippet(note.getContent()))
                .keywordScore(0.0)
                .semanticScore(0.0)
                .finalScore(0.0)
                .build();
    }

    public SearchCandidate fromFileContent(FileContent fileContent) {

        SearchResultType resultType = switch (fileContent.getExtractionType()) {
            case OCR -> SearchResultType.IMAGE;
            case PDF_TEXT -> SearchResultType.PDF;
            case PLAIN_TEXT -> SearchResultType.TEXT;
        };

        return SearchCandidate.builder()
                .id(fileContent.getFileMetadata().getId())
                .sourceType(SearchSourceType.FILE)
                .resultType(resultType)
                .title(fileContent.getFileMetadata().getFileName())
                .snippet(createSnippet(fileContent.getContent()))
                .keywordScore(0.0)
                .semanticScore(0.0)
                .finalScore(0.0)
                .build();
    }

    private String createSnippet(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        return text.length() <= 200
                ? text
                : text.substring(0, 200) + "...";
    }
}