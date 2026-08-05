package com.pkos.backend.repository.projection;

public interface KeywordSearchProjection {

    Long getNoteId();

    String getTitle();

    String getContent();

    Double getKeywordRank();

}