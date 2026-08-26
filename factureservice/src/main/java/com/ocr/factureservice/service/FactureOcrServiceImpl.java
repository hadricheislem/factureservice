package com.ocr.factureservice.service;

import com.ocr.factureservice.DTO.FactureDataDto;
import com.ocr.factureservice.entity.FactureOcr;
import com.ocr.factureservice.repository.FactureOcrRepository;
import com.ocr.factureservice.services.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FactureOcrServiceImpl implements FactureOcrService {

    @Autowired
    private FactureOcrRepository factureOcrRepository;

    @Autowired
    private OcrService ocrService;

    @Override
    public FactureOcr uploadEtSauvegarderFacture(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide !");
        }

        // 1. Extraction OCR + Parsing structuré
        FactureDataDto dataDto = null;
        String texteExtrait = "";

        try {
            dataDto = ocrService.processFacture(file.getBytes(), file.getContentType());
            if (dataDto != null && dataDto.getRawText() != null) {
                texteExtrait = dataDto.getRawText();
            }
        } catch (Exception e) {
            texteExtrait = "Erreur lors de l'extraction OCR: " + e.getMessage();
        }

        // 2. Build de l'objet FactureOcr
        FactureOcr facture = FactureOcr.builder()
                .nomFichier(file.getOriginalFilename())
                .typeFichier(file.getContentType())
                .contenuFichier(file.getBytes())
                .texteOCR(texteExtrait)
                .dateCreation(LocalDate.now())
                .statut("EXTRAIT")
                .build();

        return factureOcrRepository.save(facture);
    }

    @Override
    public List<FactureOcr> getAllFactures() {
        return factureOcrRepository.findAll();
    }

    @Override
    public Optional<FactureOcr> getFactureById(Long id) {
        return factureOcrRepository.findById(id);
    }

    @Override
    public void deleteFacture(Long id) {
        factureOcrRepository.deleteById(id);
    }
}