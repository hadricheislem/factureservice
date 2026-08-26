package com.ocr.factureservice.DTO;

import lombok.Data;

@Data
public class HeaderDataDto {
    private String numeroFacture;
    private String dateFacture;
    private String matriculeFiscal;
    private String nomFournisseur;
}