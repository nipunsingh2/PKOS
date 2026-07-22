package com.pkos.backend.service.pdf;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

public interface PdfTextExtractionService {

    /**
     * Extracts embedded text from a digital PDF using PDFBox.
     */
    String extractText(Path pdfPath);

    /**
     * Renders every page of a PDF into an image.
     * Used for OCR fallback on scanned PDFs.
     */
    List<BufferedImage> renderPages(Path pdfPath);
}