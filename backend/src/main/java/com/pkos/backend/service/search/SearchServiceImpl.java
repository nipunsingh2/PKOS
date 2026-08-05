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
import com.pkos.backend.service.search.ranking.HybridRankingService;
import lombok.RequiredArgsConstructor;
import com.pkos.backend.service.search.merge.CandidateMergeService;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final List<SearchSource> searchSources;

    private final CurrentUserService currentUserService;

    private final SearchMapper searchMapper;

    private final HybridRankingService hybridRankingService;

    private final CandidateMergeService candidateMergeService;

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

        List<SearchCandidate> mergedCandidates =
                candidateMergeService.merge(candidates);

        List<SearchCandidate> rankedCandidates =
                hybridRankingService.rank(mergedCandidates);

        List<SearchResult> results = rankedCandidates.stream()
                .map(searchMapper::fromSearchCandidate)
                .toList();

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }

}