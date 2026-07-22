package com.pkos.backend.service.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pkos.backend.dto.response.SearchResponse;
import com.pkos.backend.dto.response.SearchResult;
import com.pkos.backend.entity.FileContent;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.SearchMapper;
import com.pkos.backend.repository.FileContentRepository;
import com.pkos.backend.repository.NoteRepository;
import com.pkos.backend.service.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final NoteRepository noteRepository;

    private final FileContentRepository fileContentRepository;

    private final CurrentUserService currentUserService;

    private final SearchMapper searchMapper;

    @Override
    public SearchResponse search(
            String query,
            int page,
            int size) {

        User currentUser = currentUserService.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Note> notePage = noteRepository.searchUserNotes(
                currentUser,
                query,
                pageable);

        Page<FileContent> filePage = fileContentRepository.searchUserFiles(
                currentUser,
                query,
                pageable);

        List<SearchResult> results = new ArrayList<>();

        notePage.getContent()
                .stream()
                .map(searchMapper::fromNote)
                .forEach(results::add);

        filePage.getContent()
                .stream()
                .map(searchMapper::fromFileContent)
                .forEach(results::add);

        return SearchResponse.builder()
                .results(results)
                .totalResults(results.size())
                .build();
    }
}