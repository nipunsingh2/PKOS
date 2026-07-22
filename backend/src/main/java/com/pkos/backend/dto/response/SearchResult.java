package com.pkos.backend.dto.response;

import com.pkos.backend.entity.SearchResultType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    /**
     * NOTE, PDF, IMAGE, TEXT
     */
    private SearchResultType type;

    /**
     * ID of the underlying resource.
     */
    private Long id;

    /**
     * Display title.
     */
    private String title;

    /**
     * Short preview shown in search results.
     */
    private String snippet;

    /**
     * Which field matched the search.
     * Example:
     * title
     * content
     * OCR
     * PDF_TEXT
     */
    private String matchedField;

}