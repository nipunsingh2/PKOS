package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.entity.FileContent;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.SearchResultType;

@Component
public class SearchMapper {

    public SearchResult fromNote(Note note) {

        return SearchResult.builder()
                .type(SearchResultType.NOTE)
                .id(note.getId())
                .title(note.getTitle())
                .snippet(createSnippet(note.getContent()))
                .matchedField("content")
                .build();
    }

    public SearchResult fromFileContent(FileContent fileContent) {

        SearchResultType type = switch (fileContent.getExtractionType()) {
            case OCR -> SearchResultType.IMAGE;
            case PDF_TEXT -> SearchResultType.PDF;
            case PLAIN_TEXT -> SearchResultType.TEXT;
        };

        return SearchResult.builder()
                .type(type)
                .id(fileContent.getFileMetadata().getId())
                .title(fileContent.getFileMetadata().getFileName())
                .snippet(createSnippet(fileContent.getContent()))
                .matchedField(fileContent.getExtractionType().name())
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