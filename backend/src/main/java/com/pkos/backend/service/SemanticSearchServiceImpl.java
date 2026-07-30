package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.entity.Note;
import com.pkos.backend.mapper.SearchMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SemanticSearchServiceImpl implements SemanticSearchService {

    private final SemanticRetrievalService semanticRetrievalService;

    private final SearchMapper searchMapper;

    @Override
    public SearchResponse search(String query) {

        List<Note> notes =
                semanticRetrievalService.retrieveRelevantNotes(query);

        List<SearchResult> results = notes.stream()
                .map(searchMapper::fromNote)
                .toList();

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }

}