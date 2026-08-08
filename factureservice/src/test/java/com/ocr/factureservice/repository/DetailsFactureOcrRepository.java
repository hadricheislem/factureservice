package com.ocr.factureservice.repository;

import com.ocr.factureservice.entity.DetailsFactureOcr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailsFactureOcrRepository extends JpaRepository<DetailsFactureOcr, Long> {
}