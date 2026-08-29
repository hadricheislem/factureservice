package com.ocr.factureservice.services;

import com.ocr.factureservice.DTO.LigneFactureDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TableParserService {

    public List<LigneFactureDto> parseLignesArticles(String rawText) {
        List<LigneFactureDto> lignes = new ArrayList<>();
        if (rawText == null || rawText.isBlank()) {
            return lignes;
        }

        // Regex يتعرف على نمط السطر: [Nom Produit/Description] [Quantité] [Prix Unitaire] [Montant Total]
        // مثال: Article A  2  15.00  30.00
        Pattern pattern = Pattern.compile("(?m)^(?![Tt][Oo][Tt][Aa][Ll])(.+?)\\s+(\\d+(?:[,.]\\d+)?)\\s+(\\d+(?:[,.]\\d+)?)\\s+(\\d+(?:[,.]\\d+)?)$");
        Matcher matcher = pattern.matcher(rawText);

        while (matcher.find()) {
            try {
                String designation = matcher.group(1).trim();
                Double quantite = Double.parseDouble(matcher.group(2).replace(",", "."));
                BigDecimal prixUnitaire = new BigDecimal(matcher.group(3).replace(",", "."));
                BigDecimal montantTotal = new BigDecimal(matcher.group(4).replace(",", "."));

                // تصفية الأسطر العشوائية التي لا تمثل مقالات
                if (!designation.isEmpty() && designation.length() > 2) {
                    lignes.add(new LigneFactureDto(designation, quantite, prixUnitaire, montantTotal));
                }
            } catch (Exception ignored) {
                // في حالة فشل تحويل سطر معين ينقل للسطر التالي بدون إيقاف النظام
            }
        }

        return lignes;
    }
}