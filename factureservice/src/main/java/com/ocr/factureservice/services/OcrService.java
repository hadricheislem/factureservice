package com.ocr.factureservice.services;

import com.ocr.factureservice.DTO.FactureDataDto;
import com.ocr.factureservice.DTO.HeaderDataDto;
import com.ocr.factureservice.DTO.LigneFactureDto;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OcrService {

    private final Tesseract tesseract;

    @Autowired
    private FactureParsingService parsingService;

    @Autowired
    private HeaderParserService headerParserService;

    @Autowired
    private TableParserService tableParserService;

    public OcrService() {
        tesseract = new Tesseract();
        // Massar Tessdata
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("fra");
    }

    public FactureDataDto processFacture(byte[] fileBytes, String contentType) throws Exception {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("Le fichier envoyé est vide.");
        }

        String rawText = "";
        try {
            if (contentType != null && contentType.toLowerCase().contains("pdf")) {
                rawText = extractTextFromPdf(fileBytes);
            } else {
                rawText = extractTextFromImage(fileBytes);
            }
        } catch (Exception e) {
            System.err.println("Erreur Tesseract OCR: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Erreur Tesseract/OCR : " + e.getMessage());
        }

        // 1. Parsing général (Montants)
        FactureDataDto result = parsingService.parseFactureText(rawText);
        if (result == null) {
            result = new FactureDataDto();
            result.setRawText(rawText);
        }

        // 2. Parsing Header (En-tête)
        try {
            HeaderDataDto header = headerParserService.parseHeader(rawText);
            if (header != null) {
                if (header.getNumeroFacture() != null) {
                    result.setNumeroFacture(header.getNumeroFacture());
                }
                if (header.getMatriculeFiscal() != null) {
                    result.setMatriculeFiscal(header.getMatriculeFiscal());
                }
                if (header.getNomFournisseur() != null) {
                    result.setNomFournisseur(header.getNomFournisseur());
                }
                if (header.getDateFacture() != null && !header.getDateFacture().isBlank()) {
                    LocalDate parsedDate = parseStringToLocalDate(header.getDateFacture());
                    if (parsedDate != null) {
                        // تحويل الـ LocalDate إلى String ليتوافق مع الـ DTO والواجهة React
                        result.setDateFacture(parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        result.setDateFacture(header.getDateFacture());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Avertissement HeaderParserService: " + e.getMessage());
        }

        // 3. Parsing Articles (Tableau)
        try {
            List<LigneFactureDto> lignes = tableParserService.parseLignesArticles(rawText);
            result.setLignesArticles(lignes != null ? lignes : new ArrayList<>());
        } catch (Exception e) {
            System.err.println("Avertissement TableParserService: " + e.getMessage());
            result.setLignesArticles(new ArrayList<>());
        }

        return result;
    }

    public FactureDataDto processFacture(File file) throws Exception {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Le fichier spécifié n'existe pas.");
        }
        String rawText = tesseract.doOCR(file);
        return processParsedText(rawText);
    }

    private FactureDataDto processParsedText(String rawText) {
        FactureDataDto result = parsingService.parseFactureText(rawText);
        if (result == null) {
            result = new FactureDataDto();
            result.setRawText(rawText);
        }

        try {
            HeaderDataDto header = headerParserService.parseHeader(rawText);
            if (header != null) {
                result.setNumeroFacture(header.getNumeroFacture());
                result.setMatriculeFiscal(header.getMatriculeFiscal());
                result.setNomFournisseur(header.getNomFournisseur());
                if (header.getDateFacture() != null && !header.getDateFacture().isBlank()) {
                    LocalDate parsedDate = parseStringToLocalDate(header.getDateFacture());
                    if (parsedDate != null) {
                        result.setDateFacture(parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } else {
                        result.setDateFacture(header.getDateFacture());
                    }
                }
            }
        } catch (Exception ignored) {}

        try {
            List<LigneFactureDto> lignes = tableParserService.parseLignesArticles(rawText);
            result.setLignesArticles(lignes != null ? lignes : new ArrayList<>());
        } catch (Exception ignored) {
            result.setLignesArticles(new ArrayList<>());
        }

        return result;
    }

    private LocalDate parseStringToLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        String cleanedDate = dateStr.replaceAll("[.-]", "/").trim();
        String[] formats = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "d/M/yyyy"};

        for (String format : formats) {
            try {
                return LocalDate.parse(cleanedDate, DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String extractTextFromImage(byte[] imageBytes) throws IOException, TesseractException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            BufferedImage bufferedImage = ImageIO.read(bais);
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Format d'image non supporté ou fichier corrompu.");
            }
            return tesseract.doOCR(bufferedImage);
        }
    }

    private String extractTextFromPdf(byte[] pdfBytes) throws IOException, TesseractException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            StringBuilder fullText = new StringBuilder();

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                fullText.append(tesseract.doOCR(bufferedImage)).append("\n");
            }
            return fullText.toString();
        }
    }
}