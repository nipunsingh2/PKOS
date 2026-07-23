package com.pkos.backend.mapper;

import com.pkos.backend.dto.request.CreateTagRequest;
import com.pkos.backend.dto.request.UpdateTagRequest;
import com.pkos.backend.dto.response.TagResponse;
import com.pkos.backend.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(CreateTagRequest request) {

        Tag tag = new Tag();
        tag.setName(request.getName());

        return tag;
    }

    public void updateEntity(Tag tag, UpdateTagRequest request) {
        tag.setName(request.getName());
    }

    public TagResponse toResponse(Tag tag) {

        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}