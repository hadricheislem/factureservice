package com.ocr.factureservice;

import com.ocr.factureservice.services.OcrService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OcrServiceTest {

    @Autowired
    private OcrService ocrService;

    @Test
    void testExtractTextFromImage() throws Exception {
        // 1. Khoudh n'importe quelle image mta3 facture 3andak f-PC
        File file = new File("C:/Users/HP/Desktop/facturemodele.png");

        assertTrue(file.exists(), "Le fichier de test doit exister sur le bureau !");

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // 2. Exécution mta3 OCR
        String result = ocrService.extractText(fileBytes, "image/jpeg");

        // 3. Affichage du résultat f-Console
        System.out.println("================ TEXTE EXTRAIT ================");
        System.out.println(result);
        System.out.println("===============================================");

        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
    }
}