package com.pkos.backend.dto.response;

import com.pkos.backend.entity.enums.RelationshipType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatedNoteResponse {

    private Long noteId;

    private String title;

    private RelationshipType relationshipType;

    private double confidence;

}