package com.pkos.backend.service.search.merge;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.pkos.backend.service.search.model.SearchCandidate;

@Service
public class CandidateMergeServiceImpl implements CandidateMergeService {

    @Override
    public List<SearchCandidate> merge(List<SearchCandidate> candidates) {

        Map<String, SearchCandidate> mergedCandidates =
                new LinkedHashMap<>();

        for (SearchCandidate candidate : candidates) {

            String key = buildKey(candidate);

            SearchCandidate existing =
                    mergedCandidates.get(key);

            if (existing == null) {
                mergedCandidates.put(key, candidate);
                continue;
            }

            existing.setKeywordScore(
                    Math.max(
                            existing.getKeywordScore(),
                            candidate.getKeywordScore()));

            existing.setSemanticScore(
                    Math.max(
                            existing.getSemanticScore(),
                            candidate.getSemanticScore()));
        }

        return new ArrayList<>(mergedCandidates.values());
    }

    private String buildKey(SearchCandidate candidate) {

        return candidate.getSourceType()
                + ":"
                + candidate.getId();
    }

}