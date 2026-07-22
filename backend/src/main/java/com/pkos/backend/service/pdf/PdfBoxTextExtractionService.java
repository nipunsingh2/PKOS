package com.pkos.backend.service.pdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.rendering.PDFRenderer;

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

    @Override
    public List<BufferedImage> renderPages(Path pdfPath) {

        if (!Files.exists(pdfPath)) {
            throw new IllegalArgumentException(
                    "File does not exist: " + pdfPath
            );
        }

        try (PDDocument document = Loader.loadPDF(pdfPath.toFile())) {

            PDFRenderer renderer = new PDFRenderer(document);

            List<BufferedImage> pages = new ArrayList<>();

            for (int page = 0; page < document.getNumberOfPages(); page++) {

                BufferedImage image = renderer.renderImageWithDPI(
                        page,
                        300
                );

                pages.add(image);
            }

            return pages;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to render PDF pages.",
                    e
            );
        }
    }
}