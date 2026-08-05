package com.pkos.backend.service.search.merge;

import java.util.List;

import com.pkos.backend.service.search.model.SearchCandidate;

public interface CandidateMergeService {

    List<SearchCandidate> merge(List<SearchCandidate> candidates);

}