package com.ocr.factureservice.service;

import com.ocr.factureservice.entity.FactureOcr;
import com.ocr.factureservice.entity.DetailsFactureOcr;
import com.ocr.factureservice.repository.FactureOcrRepository;
import com.ocr.factureservice.repository.DetailsFactureOcrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FactureOcrServiceImpl implements FactureOcrService {

    private final FactureOcrRepository factureOcrRepository;
    private final DetailsFactureOcrRepository detailsFactureOcrRepository;

    @Autowired
    public FactureOcrServiceImpl(FactureOcrRepository factureOcrRepository,
                                 DetailsFactureOcrRepository detailsFactureOcrRepository) {
        this.factureOcrRepository = factureOcrRepository;
        this.detailsFactureOcrRepository = detailsFactureOcrRepository;
    }

    @Override
    @Transactional
    public FactureOcr saveFacture(FactureOcr facture) {
        // Link el details m3a el facture ken mawjoudin
        if (facture.getDetails() != null) {
            for (DetailsFactureOcr detail : facture.getDetails()) {
                detail.setFactureOcr(facture);
            }
        }
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