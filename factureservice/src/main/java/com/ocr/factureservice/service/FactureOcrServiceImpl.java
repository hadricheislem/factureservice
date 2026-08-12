package com.ocr.factureservice.service;

import com.ocr.factureservice.entity.FactureOcr;
import com.ocr.factureservice.repository.FactureOcrRepository;
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

    @Override
    public FactureOcr uploadEtSauvegarderFacture(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide !");
        }

        FactureOcr facture = FactureOcr.builder()
                .nomFichier(file.getOriginalFilename())
                .typeFichier(file.getContentType())
                .contenuFichier(file.getBytes())
                .dateCreation(LocalDate.now())
                .statut("EN_ATTENTE")
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