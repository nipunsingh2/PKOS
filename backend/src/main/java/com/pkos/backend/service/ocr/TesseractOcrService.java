package com.pkos.backend.service.ocr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.pkos.backend.config.OcrProperties;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class TesseractOcrService implements OcrService {

    private final OcrProperties properties;

    public TesseractOcrService(OcrProperties properties) {
        this.properties = properties;
    }

    @Override
    public String extractText(Path imagePath) {

        if (!Files.exists(imagePath)) {
            throw new IllegalArgumentException("File does not exist: " + imagePath);
        }

        ITesseract tesseract = new Tesseract();

        tesseract.setDatapath(properties.getDatapath());
        tesseract.setLanguage(properties.getLanguage());

        try {
            return tesseract.doOCR(imagePath.toFile()).trim();
        } catch (TesseractException e) {
            throw new RuntimeException("Failed to perform OCR.", e);
        }
    }
}