package com.pkos.backend.service.search;

import com.pkos.backend.dto.response.SearchResponse;

public interface SearchService {

    SearchResponse search(
            String query,
            int page,
            int size
    );

}