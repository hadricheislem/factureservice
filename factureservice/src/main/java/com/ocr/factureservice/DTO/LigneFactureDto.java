package com.ocr.factureservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneFactureDto {
    private String designation;
    private Double quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
}