package com.pkos.backend.service.search.source;

import java.util.List;

import com.pkos.backend.entity.User;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.model.SearchSourceType;

public interface SearchSource {

    SearchSourceType getSourceType();

    List<SearchCandidate> search(
            User user,
            String query,
            int page,
            int size
    );

}