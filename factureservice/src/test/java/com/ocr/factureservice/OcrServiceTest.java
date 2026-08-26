package com.ocr.factureservice;

import com.ocr.factureservice.DTO.FactureDataDto;
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
    void testProcessFactureWithRealFile() throws Exception {
        // 1. Path mta3 l-image 3la desktop (بدّل اسم الملف كان مش facture.jpg)
        File file = new File("C:/Users/HP/Desktop/facturemodele.png");

        assertTrue(file.exists(), "Le fichier n'a pas été trouvé sur le Bureau !");

        // 2. Lecture du fichier
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        String contentType = "image/jpeg";

        // 3. Exécution du pipeline (OCR + Header + Parsing)
        FactureDataDto result = ocrService.processFacture(fileBytes, contentType);

        // 4. Affichage des résultats dans la console
        System.out.println("==================================================");
        System.out.println("TEXTE BRUT OCR :");
        System.out.println(result.getRawText());
        System.out.println("==================================================");
        System.out.println("DONNÉES EXTRAITES STRUCTURÉES :");
        System.out.println("Fournisseur     : " + result.getNomFournisseur());
        System.out.println("N° Facture      : " + result.getNumeroFacture());
        System.out.println("Matricule Fiscal: " + result.getMatriculeFiscal());
        System.out.println("Date Facture    : " + result.getDateFacture());
        System.out.println("Montant Total   : " + result.getMontantTotal());
        System.out.println("Montant Taxe    : " + result.getMontantTaxe());
        System.out.println("==================================================");

        assertNotNull(result);
    }
}