package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.dto.search.HybridSearchResult;

public interface HybridSearchService {

    List<HybridSearchResult> search(String query);

}