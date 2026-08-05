package com.pkos.backend.mapper;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.service.search.model.SearchCandidate;

@Component
public class SearchMapper {

    public SearchResult fromSearchCandidate(SearchCandidate candidate) {

        return SearchResult.builder()
                .type(candidate.getResultType())
                .id(candidate.getId())
                .title(candidate.getTitle())
                .snippet(candidate.getSnippet())
                .matchedField(candidate.getSourceType().name())
                .build();
    }

}