package com.pkos.backend.service.search.source;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.pkos.backend.entity.FileContent;
import com.pkos.backend.entity.User;
import com.pkos.backend.mapper.SearchCandidateMapper;
import com.pkos.backend.repository.FileContentRepository;
import com.pkos.backend.service.search.model.SearchCandidate;
import com.pkos.backend.service.search.model.SearchSourceType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileSearchSource implements SearchSource {

    private final FileContentRepository fileContentRepository;

    private final SearchCandidateMapper searchCandidateMapper;

    @Override
    public SearchSourceType getSourceType() {
        return SearchSourceType.FILE;
    }

    @Override
    public List<SearchCandidate> search(
            User user,
            String query,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<FileContent> filePage = fileContentRepository.searchUserFiles(
                user,
                query,
                pageable);

        return filePage.getContent()
                .stream()
                .map(searchCandidateMapper::fromFileContent)
                .toList();
    }

}