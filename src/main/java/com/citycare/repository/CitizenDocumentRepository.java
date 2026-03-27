package com.citycare.repository;

import com.citycare.entity.CitizenDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitizenDocumentRepository extends JpaRepository<CitizenDocument, Long> {
    List<CitizenDocument> findByCitizenCitizenId(Long citizenId);
    List<CitizenDocument> findByVerificationStatus(CitizenDocument.VerificationStatus status);
}
