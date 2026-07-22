package com.pkos.backend.service.pdf;

import java.nio.file.Path;

public interface PdfTextExtractionService {

    String extractText(Path pdfPath);

}