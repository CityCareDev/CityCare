package com.citycare.repository;

import com.citycare.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    List<Audit> findByOfficerUserId(Long officerId);
    List<Audit> findByStatus(Audit.Status status);
}
