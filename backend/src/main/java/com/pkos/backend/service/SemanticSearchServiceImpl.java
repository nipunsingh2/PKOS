package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.mapper.SearchMapper;
import com.pkos.backend.dto.search.SemanticSearchResult;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final SemanticRetrievalService semanticRetrievalService;

    private final SearchMapper searchMapper;

    @Override
    public SearchResponse search(String query) {

        List<SemanticSearchResult> semanticResults =
                semanticRetrievalService.retrieveRelevantNotes(query);

        List<SearchResult> results = semanticResults.stream()
                .map(SemanticSearchResult::getNote)
                .map(searchMapper::fromNote)
                .toList();

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }

}