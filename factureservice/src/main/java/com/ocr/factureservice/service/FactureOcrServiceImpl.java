package com.ocr.factureservice.service;

import com.ocr.factureservice.DTO.FactureDataDto;
import com.ocr.factureservice.DTO.LigneFactureDto;
import com.ocr.factureservice.entity.DetailsFactureOcr;
import com.ocr.factureservice.entity.FactureOcr;
import com.ocr.factureservice.repository.DetailsFactureOcrRepository;
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
    private DetailsFactureOcrRepository detailsFactureOcrRepository;

    @Autowired
    private OcrService ocrService;

    @Override
    public FactureOcr saveFactureData(FactureDataDto dto) {
        FactureOcr facture = new FactureOcr();

        if (dto != null) {
            // 1. تحويل numeroFacture من String إلى Integer
            if (dto.getNumeroFacture() != null) {
                try {
                    String cleanNum = dto.getNumeroFacture().replaceAll("[^0-9]", "");
                    if (!cleanNum.isEmpty()) {
                        facture.setNumeroFacture(Integer.parseInt(cleanNum));
                    }
                } catch (Exception e) {
                    // في حال كان النص غير قابل للتحويل
                }
            }

            facture.setFournisseur(dto.getNomFournisseur());

            // 2. تحويل dateFacture من String إلى LocalDate
            if (dto.getDateFacture() != null) {
                try {
                    String dateStr = dto.getDateFacture().trim();
                    if (dateStr.contains("/")) {
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            int d = Integer.parseInt(parts[0]);
                            int m = Integer.parseInt(parts[1]);
                            int y = Integer.parseInt(parts[2]);
                            facture.setDateFacture(LocalDate.of(y, m, d));
                        }
                    } else if (dateStr.contains("-")) {
                        facture.setDateFacture(LocalDate.parse(dateStr));
                    }
                } catch (Exception e) {
                    facture.setDateFacture(LocalDate.now());
                }
            }

            // 3. تعديل المسميات لتطابق Getters/Setters في الـ Entity (HT و TTC بأحرف كبيرة)
            facture.setMontantHT(dto.getMontantHT());
            facture.setMontantTTC(dto.getMontantTotal());
        }

        facture.setDateCreation(LocalDate.now());
        facture.setStatut("ENREGISTRE");

        // حفظ الفاتورة الرئيسية
        FactureOcr savedFacture = factureOcrRepository.save(facture);

        // حفظ الأسطر التفصيلية إن وجدت
        if (dto != null && dto.getLignesArticles() != null && !dto.getLignesArticles().isEmpty()) {
            for (LigneFactureDto ligneDto : dto.getLignesArticles()) {
                DetailsFactureOcr detail = new DetailsFactureOcr();
                detail.setDesignation(ligneDto.getDesignation());
                detail.setQuantite(ligneDto.getQuantite());
                detail.setFactureOcr(savedFacture);
                detailsFactureOcrRepository.save(detail);
            }
        }

        return savedFacture;
    }

    @Override
    public FactureOcr uploadEtSauvegarderFacture(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier téléchargé est vide !");
        }

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