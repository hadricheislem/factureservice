package com.ocr.factureservice.controller;

import com.ocr.factureservice.entity.FactureOcr;
import com.ocr.factureservice.service.FactureOcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "*") // Permet à React de communiquer avec Spring Boot sans erreur CORS
public class FactureOcrController {

    @Autowired
    private FactureOcrService factureOcrService;

    // 1. POST /api/factures/upload : Pour uploader un fichier PDF/Image
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFacture(@RequestParam("file") MultipartFile file) {
        try {
            FactureOcr savedFacture = factureOcrService.uploadEtSauvegarderFacture(file);
            return new ResponseEntity<>(savedFacture, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de l'upload du fichier: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. GET /api/factures : Pour récupérer la liste de toutes les factures
    @GetMapping
    public ResponseEntity<List<FactureOcr>> getAllFactures() {
        List<FactureOcr> factures = factureOcrService.getAllFactures();
        return ResponseEntity.ok(factures);
    }

    // 3. GET /api/factures/{id} : Pour récupérer une facture spécifique par son ID
    @GetMapping("/{id}")
    public ResponseEntity<FactureOcr> getFactureById(@PathVariable Long id) {
        return factureOcrService.getFactureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. DELETE /api/factures/{id} : Pour supprimer une facture par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureOcrService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
}