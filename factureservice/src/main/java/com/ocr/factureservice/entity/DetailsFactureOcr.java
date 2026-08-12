package com.ocr.factureservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detailsfactureocr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailsFactureOcr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroLigne;

    private String codeArticle;

    private String designation;

    // --- CORRECTION 1 : Sans accent 'é' pour la base SQL Server ---
    @Column(name = "quantite")
    private Double quantite;

    private Double prixUnitaireHT;

    private Double tauxRemise;

    private Double montantRemise;

    private Double tauxTVA;

    private Double montantTVA;

    private Double montantHT;

    private Double montantTTC;

    // --- CORRECTION 2 : @JsonIgnore pour éviter la boucle infinie dans l'API REST ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id")
    @JsonIgnore
    private FactureOcr factureOcr;
}