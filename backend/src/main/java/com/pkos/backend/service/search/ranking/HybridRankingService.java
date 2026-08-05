package com.pkos.backend.service.search.ranking;

import java.util.List;

import com.pkos.backend.service.search.model.SearchCandidate;

public interface HybridRankingService {

    List<SearchCandidate> rank(List<SearchCandidate> candidates);

}