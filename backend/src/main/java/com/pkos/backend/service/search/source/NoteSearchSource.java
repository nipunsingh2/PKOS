package com.pkos.backend.service.search.source;

import java.util.List;

import org.springframework.stereotype.Component;

import com.pkos.backend.entity.SearchResultType;
import com.pkos.backend.entity.User;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.repository.projection.KeywordSearchProjection;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.model.SearchSourceType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoteSearchSource implements SearchSource {

    private static final int SNIPPET_LENGTH = 200;

    private final NoteRepository noteRepository;

    @Override
    public SearchSourceType getSourceType() {
        return SearchSourceType.NOTE;
    }

    @Override
    public List<SearchCandidate> search(
            User user,
            String query,
            int page,
            int size) {

        int limit = (page + 1) * size;

        List<KeywordSearchProjection> results =
                noteRepository.keywordSearch(
                        user.getId(),
                        query,
                        limit);

        return results.stream()
                .map(this::toSearchCandidate)
                .toList();
    }

    private SearchCandidate toSearchCandidate(
            KeywordSearchProjection projection) {               

        return SearchCandidate.builder()
                .id(projection.getNoteId())
                .sourceType(SearchSourceType.NOTE)
                .resultType(SearchResultType.NOTE)
                .title(projection.getTitle())
                .snippet(createSnippet(projection.getContent()))
                .keywordScore(projection.getKeywordRank())
                .semanticScore(0.0)
                .finalScore(0.0)
                .build();
    }

    private String createSnippet(String text) {

        if (text == null || text.isBlank()) {
            return "";
        }

        return text.length() <= SNIPPET_LENGTH
                ? text
                : text.substring(0, SNIPPET_LENGTH) + "...";
    }

}