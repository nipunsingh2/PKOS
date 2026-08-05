package com.pkos.backend.service.search.source;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pkos.backend.dto.search.SemanticSearchResult;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.SearchCandidateMapper;
import com.pkos.backend.service.SemanticRetrievalService;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.model.SearchSourceType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SemanticSearchSource implements SearchSource {

    private final SemanticRetrievalService semanticRetrievalService;

    private final SearchCandidateMapper searchCandidateMapper;

    @Override
    public SearchSourceType getSourceType() {
        return SearchSourceType.NOTE;
    }

    @Override
    public List<SearchCandidate> search(
            User user,
            String query,
            int page,
            int size) {

        List<SemanticSearchResult> semanticResults =
                semanticRetrievalService.retrieveRelevantNotes(query);

        return semanticResults.stream()
                .map(searchCandidateMapper::fromSemanticSearchResult)
                .toList();
    }

}