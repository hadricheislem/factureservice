package com.ocr.factureservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureDataDto {
    private String rawText;

    // المبالغ الأساسية
    private Double montantTotal; // أو montantTTC
    private Double montantTTC;   // مضافة لمطابقة الـ Frontend
    private Double montantTaxe;  // أو montantTVA
    private Double montantTVA;   // مضافة لمطابقة الـ Frontend
    private Double montantHT;    // مضافة لمطابقة الـ Frontend

    // بيانات الرأس (Header)
    private String numeroFacture;
    private String dateFacture;  // String لتفادي أخطاء الـ Parsing من الـ OCR
    private String matriculeFiscal;
    private String nomFournisseur;
    private String fournisseur;   // alias مطابقة لـ React

    @Builder.Default
    private List<LigneFactureDto> lignesArticles = new ArrayList<>();
}