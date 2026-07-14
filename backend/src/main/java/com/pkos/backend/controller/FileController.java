package com.pkos.backend.controller;

import com.pkos.backend.dto.response.FileResponse;
import com.pkos.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        return ResponseEntity.ok(
                fileService.uploadFile(file)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id)
            throws Exception {

        Resource resource = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                resource.getFilename() + "\""
                )
                .body(resource);
    } 

}