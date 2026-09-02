package com.ocr.factureservice.controller;

import com.ocr.factureservice.DTO.FactureDataDto;
import com.ocr.factureservice.entity.FactureOcr;
import com.ocr.factureservice.service.FactureOcrService;
import com.ocr.factureservice.services.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "*")
public class FactureOcrController {

    @Autowired
    private FactureOcrService factureOcrService;

    @Autowired
    private OcrService ocrService;

    // 1. POST /api/factures/import : Extraction OCR
    @PostMapping("/import")
    public ResponseEntity<?> importFacture(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier envoyé est vide.");
        }
        try {
            String contentType = file.getContentType();
            FactureDataDto extractedData = ocrService.processFacture(file.getBytes(), contentType);
            return ResponseEntity.ok(extractedData);
        } catch (Exception e) {
            e.printStackTrace(); // طباعة الخطأ كاملاً في Terminal لسهولة التتبع

            // إرجاع كائن JSON منظم للـ React يحتوي على تفاصيل الخطأ
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors du traitement OCR");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 2. POST /api/factures/upload : Sauvegarde directe
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFacture(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier envoyé est vide.");
        }
        try {
            FactureOcr savedFacture = factureOcrService.uploadEtSauvegarderFacture(file);
            return new ResponseEntity<>(savedFacture, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de l'upload du fichier: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. GET /api/factures : Liste de toutes les factures
    @GetMapping
    public ResponseEntity<List<FactureOcr>> getAllFactures() {
        List<FactureOcr> factures = factureOcrService.getAllFactures();
        return ResponseEntity.ok(factures);
    }

    // 4. GET /api/factures/{id} : Facture par ID
    @GetMapping("/{id}")
    public ResponseEntity<FactureOcr> getFactureById(@PathVariable Long id) {
        return factureOcrService.getFactureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. DELETE /api/factures/{id} : Suppression par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureOcrService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
}