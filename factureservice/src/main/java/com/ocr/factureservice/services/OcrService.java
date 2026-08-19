package com.ocr.factureservice.services;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService() {
        tesseract = new Tesseract();
        // Path mta3 dossier tessdata 3la machintak
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("fra"); // Langue par défaut pour les factures
    }

    public String extractText(byte[] fileBytes, String contentType) throws Exception {
        if ("application/pdf".equals(contentType)) {
            return extractTextFromPdf(fileBytes);
        } else {
            return extractTextFromImage(fileBytes);
        }
    }

    // 1. Extraction mel Images (PNG, JPG, JPEG, etc.)
    private String extractTextFromImage(byte[] imageBytes) throws IOException, TesseractException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bais);
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Format d'image non supporté ou fichier corrompu.");
        }
        return tesseract.doOCR(bufferedImage);
    }

    // 2. Extraction mel PDF (Convertir les pages en images puis OCR)
    private String extractTextFromPdf(byte[] pdfBytes) throws IOException, TesseractException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            StringBuilder fullText = new StringBuilder();

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300); // 300 DPI pour une meilleure précision
                fullText.append(tesseract.doOCR(bufferedImage)).append("\n");
            }
            return fullText.toString();
        }
    }
}