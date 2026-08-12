package com.ocr.factureservice.service;

import com.ocr.factureservice.entity.FactureOcr;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FactureOcrService {
    FactureOcr uploadEtSauvegarderFacture(MultipartFile file) throws IOException;
    List<FactureOcr> getAllFactures();
    Optional<FactureOcr> getFactureById(Long id);
    void deleteFacture(Long id);
}