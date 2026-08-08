package com.ocr.factureservice.service;

import com.ocr.factureservice.entity.FactureOcr;
import java.util.List;
import java.util.Optional;

public interface FactureOcrService {

    FactureOcr saveFacture(FactureOcr facture);

    List<FactureOcr> getAllFactures();

    Optional<FactureOcr> getFactureById(Long id);

    void deleteFacture(Long id);
}