package com.pkos.backend.service.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.SearchMapper;
import com.pkos.backend.service.CurrentUserService;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.source.SearchSource;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final List<SearchSource> searchSources;

    private final CurrentUserService currentUserService;

    private final SearchMapper searchMapper;

    @Override
    public SearchResponse search(
            String query,
            int page,
            int size) {

        User currentUser = currentUserService.getCurrentUser();

        List<SearchCandidate> candidates = new ArrayList<>();

        for (SearchSource searchSource : searchSources) {
            candidates.addAll(
                    searchSource.search(
                            currentUser,
                            query,
                            page,
                            size));
        }

        List<SearchResult> results = candidates.stream()
                .map(searchMapper::fromSearchCandidate)
                .toList();

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }

}