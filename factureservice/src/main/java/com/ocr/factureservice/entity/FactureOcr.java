package com.ocr.factureservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factureocr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureOcr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;

    private String typeFichier;

    // --- CORRECTION 1 : Stockage binaire compatible MySQL (LONGBLOB) ---
    @Lob
    @Column(name = "contenu_fichier", columnDefinition = "LONGBLOB")
    private byte[] contenuFichier;

    private Integer numeroFacture;

    private LocalDate dateFacture;

    private String fournisseur;

    private String matriculeFiscale;

    private Double montantFiscale;

    private Double montantHT;

    private Double montantRemise;

    private Double timbreFiscale;

    private Double montantTTC;

    // --- CORRECTION 2 : Texte long extrait par OCR compatible MySQL (LONGTEXT) ---
    @Lob
    @Column(name = "texteocr", columnDefinition = "LONGTEXT")
    private String texteOCR;

    private String statut;

    private LocalDate dateCreation;

    // Relation avec les détails de la facture
    @OneToMany(mappedBy = "factureOcr", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetailsFactureOcr> details = new ArrayList<>();
}