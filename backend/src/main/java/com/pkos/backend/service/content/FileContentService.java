package com.pkos.backend.service.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.ExtractionType;
import com.pkos.backend.entity.FileContent;
import com.pkos.backend.entity.FileMetadata;
import com.pkos.backend.entity.SupportedFileType;
import com.pkos.backend.repository.FileContentRepository;
import com.pkos.backend.service.ocr.OcrService;
import com.pkos.backend.service.pdf.PdfTextExtractionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileContentService {

    private static final Logger logger =
            LoggerFactory.getLogger(FileContentService.class);

    private final OcrService ocrService;
    private final PdfTextExtractionService pdfTextExtractionService;
    private final FileContentRepository fileContentRepository;

    public void process(FileMetadata fileMetadata) {

        try {

            SupportedFileType fileType =
                    SupportedFileType.from(fileMetadata.getContentType());

            switch (fileType) {

                case IMAGE -> saveContent(
                        fileMetadata,
                        extractImageContent(fileMetadata),
                        ExtractionType.OCR
                );

                case TEXT -> saveContent(
                        fileMetadata,
                        extractTextContent(fileMetadata),
                        ExtractionType.PLAIN_TEXT
                );

                case PDF -> {
                        String pdfText = extractPdfContent(fileMetadata);
                        if (hasMeaningfulText(pdfText)) {
                                saveContent(
                                        fileMetadata,
                                        pdfText,
                                        ExtractionType.PDF_TEXT
                                );
                        } else {
                                logger.info(
                                        "No embedded text found in '{}'. Falling back to OCR.",
                                        fileMetadata.getFileName()
                                );
                                saveContent(
                                        fileMetadata,
                                        extractScannedPdfContent(fileMetadata),
                                        ExtractionType.OCR
                                );
                        }
                        }

                case UNKNOWN -> logger.warn(
                        "Skipping unsupported file type '{}' for file '{}'",
                        fileMetadata.getContentType(),
                        fileMetadata.getFileName()
                );
            }

        } catch (Exception ex) {

            logger.error(
                    "Content extraction failed for file '{}'",
                    fileMetadata.getFileName(),
                    ex
            );
        }
    }

    private String extractImageContent(FileMetadata fileMetadata) {

        return ocrService.extractText(
                Path.of(fileMetadata.getStoragePath())
        );
    }

    private String extractTextContent(FileMetadata fileMetadata)
            throws IOException {

        return Files.readString(
                Path.of(fileMetadata.getStoragePath())
        );
    }

        private String extractPdfContent(FileMetadata fileMetadata) {

        return pdfTextExtractionService.extractText(
                Path.of(fileMetadata.getStoragePath())
        );
        } 

    private void saveContent(
            FileMetadata fileMetadata,
            String content,
            ExtractionType extractionType) {

        if (content == null || content.isBlank()) {
            logger.warn(
                    "No content extracted from file '{}'",
                    fileMetadata.getFileName()
            );
            return;
        }

        FileContent fileContent = FileContent.builder()
                .content(content)
                .extractionType(extractionType)
                .build();

        fileMetadata.setFileContent(fileContent);

        fileContentRepository.save(fileContent);
        logger.info(
                "Content extracted successfully for file '{}'",
                fileMetadata.getFileName()
        );
    }

        private boolean hasMeaningfulText(String text) {

        return text != null
                && !text.isBlank()
                && text.trim().length() >= 10;
        }

        private String extractScannedPdfContent(FileMetadata fileMetadata) {

        StringBuilder extractedText = new StringBuilder();

        pdfTextExtractionService
                .renderPages(Path.of(fileMetadata.getStoragePath()))
                .forEach(page -> {

                        extractedText.append(
                                ocrService.extractText(page)
                        );

                        extractedText.append(System.lineSeparator());
                        extractedText.append(System.lineSeparator());

                });

        return extractedText.toString().trim();
        }
}