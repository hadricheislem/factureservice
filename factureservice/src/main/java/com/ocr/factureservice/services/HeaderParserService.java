package com.ocr.factureservice.services;

import com.ocr.factureservice.DTO.HeaderDataDto;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HeaderParserService {

    public HeaderDataDto parseHeader(String rawText) {
        HeaderDataDto header = new HeaderDataDto();

        // 1. تقطيع النص والتركيز على الأسطر الأولى فقط (Header Zone)
        String[] lines = rawText.split("\r?\n");
        StringBuilder headerTextBuilder = new StringBuilder();
        int maxLines = Math.min(lines.length, 15); // أول 15 سطر فقط
        for (int i = 0; i < maxLines; i++) {
            headerTextBuilder.append(lines[i]).append("\n");
        }
        String headerText = headerTextBuilder.toString();

        // 2. Extraction du Numéro de Facture
        Pattern numPattern = Pattern.compile("(?i)(?:facture|fac|inv|n°|ref)\\s*[:.-]?\s*([A-Z0-9/-]{3,20})");
        Matcher numMatcher = numPattern.matcher(headerText);
        if (numMatcher.find()) {
            header.setNumeroFacture(numMatcher.group(1));
        }

        // 3. Extraction de la Date
        Pattern datePattern = Pattern.compile("\\b(\\d{2}[/.-]\\d{2}[/.-]\\d{4}|\\d{4}[/.-]\\d{2}[/.-]\\d{2})\\b");
        Matcher dateMatcher = datePattern.matcher(headerText);
        if (dateMatcher.find()) {
            header.setDateFacture(dateMatcher.group(1));
        }

        // 4. Extraction du Matricule Fiscal (MF)
        Pattern mfPattern = Pattern.compile("(?i)(?:mf|mat\\.?\\s*fisc|tva)\\s*[:.-]?\\s*([0-9]{7}[A-Z]{3}[0-9]{3}|[0-9A-Z/-]{8,15})");
        Matcher mfMatcher = mfPattern.matcher(headerText);
        if (mfMatcher.find()) {
            header.setMatriculeFiscal(mfMatcher.group(1));
        }

        // 5. Extraction du Nom du Fournisseur (أول سطر غير فارغ يحتوي على نص)
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.length() > 3 && !trimmed.matches("(?i).*facture.*")) {
                header.setNomFournisseur(trimmed);
                break;
            }
        }

        return header;
    }
}