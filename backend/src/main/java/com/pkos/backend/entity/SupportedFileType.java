package com.pkos.backend.entity;

import java.util.Set;

public enum SupportedFileType {

    IMAGE(Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg"
    )),

    TEXT(Set.of(
            "text/plain"
    )),

    PDF(Set.of(
            "application/pdf"
    )),

    UNKNOWN(Set.of());

    private final Set<String> mimeTypes;

    SupportedFileType(Set<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public static SupportedFileType from(String contentType) {

        if (contentType == null || contentType.isBlank()) {
            return UNKNOWN;
        }

        for (SupportedFileType type : values()) {
            if (type.mimeTypes.contains(contentType)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}