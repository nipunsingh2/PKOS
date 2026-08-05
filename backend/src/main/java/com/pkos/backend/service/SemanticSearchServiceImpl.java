package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.dto.search.SemanticSearchResult;
import com.pkos.backend.mapper.SearchCandidateMapper;
import com.pkos.backend.mapper.SearchMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final SemanticRetrievalService semanticRetrievalService;

    private final SearchCandidateMapper searchCandidateMapper;

    private final SearchMapper searchMapper;

    @Override
    public SearchResponse search(String query) {

        List<SemanticSearchResult> semanticResults =
                semanticRetrievalService.retrieveRelevantNotes(query);

        List<SearchResult> results = semanticResults.stream()
                .map(SemanticSearchResult::getNote)
                .map(searchCandidateMapper::fromNote)
                .map(searchMapper::fromSearchCandidate)
                .toList();

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }

}