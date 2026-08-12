package com.ocr.factureservice.repository;

import com.ocr.factureservice.entity.FactureOcr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureOcrRepository extends JpaRepository<FactureOcr, Long> {
}