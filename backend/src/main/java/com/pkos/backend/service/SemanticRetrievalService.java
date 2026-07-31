package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.dto.search.SemanticSearchResult;

public interface SemanticRetrievalService {

    List<SemanticSearchResult> retrieveRelevantNotes(String query);
}