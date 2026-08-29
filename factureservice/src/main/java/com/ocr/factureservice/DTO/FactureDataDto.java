package com.ocr.factureservice.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class FactureDataDto {
    private String rawText;
    private Double montantTotal;
    private Double montantTaxe;


    private String numeroFacture;
    private LocalDate dateFacture;
    private String matriculeFiscal;
    private String nomFournisseur;

    private List<LigneFactureDto> lignesArticles = new ArrayList<>();
}