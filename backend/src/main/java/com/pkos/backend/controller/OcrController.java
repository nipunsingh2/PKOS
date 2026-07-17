package com.pkos.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pkos.backend.service.ocr.OcrService;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping(
            value = "/test",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> testOcr(
            @RequestParam("file") MultipartFile file) throws IOException {

        Path tempFile = Files.createTempFile("ocr-", "-" + file.getOriginalFilename());

        try {
            file.transferTo(tempFile);

            String extractedText = ocrService.extractText(tempFile);

            return ResponseEntity.ok(extractedText);

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}