package com.pkos.backend.service.pdf;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PdfBoxTextExtractionServiceTest {

    @Autowired
    private PdfTextExtractionService pdfTextExtractionService;

    @Test
    void shouldExtractTextFromPdf() throws URISyntaxException {

        Path pdfPath = Paths.get(
                getClass()
                        .getClassLoader()
                        .getResource("pdf-test.pdf")
                        .toURI());

        String extractedText = pdfTextExtractionService.extractText(pdfPath);

        System.out.println("========== PDF TEXT ==========");
        System.out.println(extractedText);
        System.out.println("==============================");

        assertFalse(extractedText.isBlank());
    }
}