package com.pkos.backend.service.pdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class PdfBoxTextExtractionService implements PdfTextExtractionService {

    @Override
    public String extractText(Path pdfPath) {

        if (!Files.exists(pdfPath)) {
            throw new IllegalArgumentException("File does not exist: " + pdfPath);
        }

        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document).trim();

        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from PDF.", e);
        }
    }
}