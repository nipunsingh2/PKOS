package com.pkos.backend.service;

import com.pkos.backend.dto.response.SearchResponse;

public interface SemanticSearchService {

    SearchResponse search(String query);

}