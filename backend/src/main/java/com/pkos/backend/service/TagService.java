package com.pkos.backend.service;

import com.pkos.backend.dto.request.CreateTagRequest;
import com.pkos.backend.dto.request.UpdateTagRequest;
import com.pkos.backend.dto.response.TagResponse;
import com.pkos.backend.entity.Note;
import com.pkos.backend.entity.Tag;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.DuplicateResourceException;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.TagMapper;
import com.pkos.backend.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class TagService {

    private static final Logger logger =
            LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public TagService(
            TagRepository tagRepository,
            TagMapper tagMapper,
            CurrentUserService currentUserService,
            AuditService auditService) {

        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @Transactional
    public TagResponse createTag(CreateTagRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        String normalizedName = normalizeTagName(request.getName());

        if (tagRepository.existsByNameIgnoreCaseAndUser(
                normalizedName,
                currentUser)) {

            throw new DuplicateResourceException(
                    "Tag already exists."
            );
        }

        request.setName(normalizedName);

        Tag tag = tagMapper.toEntity(request);

        tag.setUser(currentUser);

        Tag savedTag = tagRepository.save(tag);

        auditService.logEvent(
                "Created Tag",
                currentUser.getEmail()
        );

        logger.info(
                "Tag created successfully. Tag ID: {}, User: {}",
                savedTag.getId(),
                currentUser.getEmail()
        );

        return tagMapper.toResponse(savedTag);
    }

    @Transactional(readOnly = true)
    public Page<TagResponse> getTags(
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser = currentUserService.getCurrentUser();

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return tagRepository
                .findByUser(currentUser, pageable)
                .map(tagMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Tag tag = findOwnedTag(id, currentUser);

        return tagMapper.toResponse(tag);
    }

    @Transactional
    public TagResponse updateTag(
            Long id,
            UpdateTagRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Tag tag = findOwnedTag(id, currentUser);

        String normalizedName = normalizeTagName(request.getName());

        if (tagRepository.existsByNameIgnoreCaseAndUserAndIdNot(
                normalizedName,
                currentUser,
                id)) {

            throw new DuplicateResourceException(
                    "Tag already exists."
            );
        }

        request.setName(normalizedName);

        tagMapper.updateEntity(tag, request);

        Tag updatedTag = tagRepository.save(tag);

        auditService.logEvent(
                "Updated Tag",
                currentUser.getEmail()
        );

        logger.info(
                "Tag updated successfully. Tag ID: {}, User: {}",
                updatedTag.getId(),
                currentUser.getEmail()
        );

        return tagMapper.toResponse(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Tag tag = findOwnedTag(id, currentUser);

        for (Note note : tag.getNotes()) {
            note.getTags().remove(tag);
        }

        tag.getNotes().clear();

        auditService.logEvent(
                "Deleted Tag",
                currentUser.getEmail()
        );

        logger.info(
                "Tag deleted successfully. Tag ID: {}, User: {}",
                tag.getId(),
                currentUser.getEmail()
        );

        tagRepository.delete(tag);
    }

    private Tag findOwnedTag(Long id, User user) {

        return tagRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tag not found."
                        ));
    }

    private String normalizeTagName(String name) {
        return name.trim();
    }
}