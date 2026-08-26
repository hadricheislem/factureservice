package com.ocr.factureservice.services;

import com.ocr.factureservice.DTO.FactureDataDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FactureParsingService {

    public FactureDataDto parseFactureText(String rawText) {
        FactureDataDto dto = new FactureDataDto();
        dto.setRawText(rawText);

        if (rawText == null || rawText.isBlank()) {
            return dto;
        }

        // Conversion BigDecimal -> Double avec gestion du null
        BigDecimal total = extractMontantTotal(rawText);
        BigDecimal taxe = extractMontantTaxe(rawText);

        dto.setMontantTotal(total != null ? total.doubleValue() : null);
        dto.setMontantTaxe(taxe != null ? taxe.doubleValue() : null);
        dto.setDateFacture(extractDate(rawText));

        return dto;
    }

    private BigDecimal extractMontantTotal(String text) {
        Pattern pattern = Pattern.compile("TOTAL[\\s\\S]*?(\\d+([,.]\\d{1,2})?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String valueStr = matcher.group(1).replace(",", ".");
            try {
                return new BigDecimal(valueStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private BigDecimal extractMontantTaxe(String text) {
        Pattern pattern = Pattern.compile("(?:TAXE|TVA)[\\s\\S]*?(\\d+[,.]\\d{2})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String valueStr = matcher.group(1).replace(",", ".");
            try {
                return new BigDecimal(valueStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private LocalDate extractDate(String text) {
        Pattern pattern = Pattern.compile("(\\d{2}[/-]\\d{2}[/-]\\d{4})");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String dateStr = matcher.group(1).replace("-", "/");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}