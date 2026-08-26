package com.ocr.factureservice.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FactureDataDto {
    private String rawText;
    private Double montantTotal;
    private Double montantTaxe;

    // Champs Header (اللازمة للـ Header)
    private String numeroFacture;
    private LocalDate dateFacture;
    private String matriculeFiscal;
    private String nomFournisseur;
}