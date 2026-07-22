package com.pkos.backend.service.ocr;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.awt.image.BufferedImage;

public interface OcrService {

    String extractText(Path imagePath);

    String extractText(BufferedImage image);

}