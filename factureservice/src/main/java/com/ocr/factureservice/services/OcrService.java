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
import java.util.List;

@Service
public class OcrService {

    private final Tesseract tesseract;

    @Autowired
    private FactureParsingService parsingService;

    @Autowired
    private HeaderParserService headerParserService;

    @Autowired
    private TableParserService tableParserService; // Injection du service d'extraction des articles

    public OcrService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("fra");
    }

    public FactureDataDto processFacture(byte[] fileBytes, String contentType) throws Exception {
        String rawText;
        if ("application/pdf".equals(contentType)) {
            rawText = extractTextFromPdf(fileBytes);
        } else {
            rawText = extractTextFromImage(fileBytes);
        }

        // 1. Parsing général (Montants)
        FactureDataDto result = parsingService.parseFactureText(rawText);

        // 2. Parsing de l'en-tête (Header)
        HeaderDataDto header = headerParserService.parseHeader(rawText);

        // 3. Parsing des lignes d'articles (Tableau)
        List<LigneFactureDto> lignes = tableParserService.parseLignesArticles(rawText);
        result.setLignesArticles(lignes);

        // 4. Mapping des données du Header
        result.setNumeroFacture(header.getNumeroFacture());
        result.setMatriculeFiscal(header.getMatriculeFiscal());
        result.setNomFournisseur(header.getNomFournisseur());

        // Conversion String -> LocalDate
        if (header.getDateFacture() != null && !header.getDateFacture().isEmpty()) {
            result.setDateFacture(parseStringToLocalDate(header.getDateFacture()));
        }

        return result;
    }

    public FactureDataDto processFacture(File file) throws Exception {
        String rawText = tesseract.doOCR(file);

        FactureDataDto result = parsingService.parseFactureText(rawText);
        HeaderDataDto header = headerParserService.parseHeader(rawText);

        // Parsing des articles
        List<LigneFactureDto> lignes = tableParserService.parseLignesArticles(rawText);
        result.setLignesArticles(lignes);

        result.setNumeroFacture(header.getNumeroFacture());
        result.setMatriculeFiscal(header.getMatriculeFiscal());
        result.setNomFournisseur(header.getNomFournisseur());

        if (header.getDateFacture() != null && !header.getDateFacture().isEmpty()) {
            result.setDateFacture(parseStringToLocalDate(header.getDateFacture()));
        }

        return result;
    }

    // Méthode utilitaire pour convertir les différents formats de date
    private LocalDate parseStringToLocalDate(String dateStr) {
        String cleanedDate = dateStr.replaceAll("[.-]", "/");
        String[] formats = {"dd/MM/yyyy", "yyyy/MM/dd", "d/M/yyyy"};

        for (String format : formats) {
            try {
                return LocalDate.parse(cleanedDate, DateTimeFormatter.ofPattern(format));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String extractTextFromImage(byte[] imageBytes) throws IOException, TesseractException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bais);
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Format d'image non supporté ou fichier corrompu.");
        }
        return tesseract.doOCR(bufferedImage);
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