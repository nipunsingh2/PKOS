package com.pkos.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pkos.backend.dto.request.SemanticSearchRequest;
import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.service.SemanticSearchService;
import com.pkos.backend.service.HybridSearchService;
import com.pkos.backend.service.search.SearchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    private final SemanticSearchService semanticSearchService;

    private final HybridSearchService hybridSearchService;

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        SearchResponse response =
                searchService.search(q, page, size);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/semantic")
    public ResponseEntity<SearchResponse> semanticSearch(
            @Valid @RequestBody SemanticSearchRequest request) {

        SearchResponse response =
                semanticSearchService.search(
                        request.getQuery()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/hybrid")
    public ResponseEntity<SearchResponse> hybridSearch(
            @Valid @RequestBody SemanticSearchRequest request) {

        var hybridResults =
                hybridSearchService.search(
                        request.getQuery()
                );

        SearchResponse response =
                SearchResponse.builder()
                        .results(
                                hybridResults.stream()
                                        .map(result ->
                                                com.pkos.backend.dto.response.SearchResult
                                                        .builder()
                                                        .type(
                                                                com.pkos.backend.entity.SearchResultType.NOTE
                                                        )
                                                        .id(
                                                                result.getNote().getId()
                                                        )
                                                        .title(
                                                                result.getNote().getTitle()
                                                        )
                                                        .snippet(
                                                                result.getNote().getContent()
                                                        )
                                                        .matchedField("HYBRID")
                                                        .build()
                                        )
                                        .toList()
                        )
                        .totalResults(hybridResults.size())
                        .build();

        return ResponseEntity.ok(response);
    }
}