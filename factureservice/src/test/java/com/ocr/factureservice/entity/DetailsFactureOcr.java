package com.ocr.factureservice.entity;

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

    @Column(name = "quantité")
    private Double quantite;

    private Double prixUnitaireHT;

    private Double tauxRemise;

    private Double montantRemise;

    private Double tauxTVA;

    private Double montantTVA;

    private Double montantHT;

    private Double montantTTC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id")
    private FactureOcr factureOcr;
}