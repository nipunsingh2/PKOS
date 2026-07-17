package com.pkos.backend.service.ocr;

import java.nio.file.Path;

public interface OcrService {

    String extractText(Path imagePath);

}