package com.citycare.repository;

import com.citycare.entity.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByType(ComplianceRecord.EntityType type);
    List<ComplianceRecord> findByEntityId(Long entityId);
    List<ComplianceRecord> findByResult(ComplianceRecord.Result result);
    List<ComplianceRecord> findByOfficerUserId(Long officerId);
}
