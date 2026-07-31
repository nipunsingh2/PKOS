package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.dto.search.KeywordSearchResult;
import com.pkos.backend.entity.User;

public interface KeywordSearchService {

    List<KeywordSearchResult> search(
            User user,
            String query,
            int limit
    );

}