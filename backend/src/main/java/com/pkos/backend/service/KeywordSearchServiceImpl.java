package com.pkos.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.dto.search.KeywordSearchResult;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.User;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.repository.projection.KeywordSearchProjection;
import com.pkos.backend.service.KeywordSearchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordSearchServiceImpl implements KeywordSearchService {

    private final NoteRepository noteRepository;

    @Override
    public List<KeywordSearchResult> search(
            User user,
            String query,
            int limit
    ) {

        List<KeywordSearchProjection> projections =
                noteRepository.keywordSearch(
                        user.getId(),
                        query,
                        limit
                );

        List<Long> noteIds = projections.stream()
                .map(KeywordSearchProjection::getNoteId)
                .toList();

        List<Note> notes = noteRepository.findAllByIdIn(noteIds);

        Map<Long, Note> noteMap = new HashMap<>();

        for (Note note : notes) {
            noteMap.put(note.getId(), note);
        }

        return projections.stream()
                .map(projection -> new KeywordSearchResult(
                        noteMap.get(projection.getNoteId()),
                        projection.getKeywordRank()
                ))
                .toList();
    }

}