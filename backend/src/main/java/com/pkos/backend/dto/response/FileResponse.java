package com.pkos.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {

    private Long id;

    private String fileName;

    private String contentType;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    private String downloadUrl;
}