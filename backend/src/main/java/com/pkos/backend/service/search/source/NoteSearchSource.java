package com.pkos.backend.service.search.source;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.SearchCandidateMapper;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.model.SearchSourceType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoteSearchSource implements SearchSource {

    private final NoteRepository noteRepository;

    private final SearchCandidateMapper searchCandidateMapper;

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

        Pageable pageable = PageRequest.of(page, size);

        Page<Note> notePage = noteRepository.searchUserNotes(
                user,
                query,
                pageable);

        return notePage.getContent()
                .stream()
                .map(searchCandidateMapper::fromNote)
                .toList();
    }

}