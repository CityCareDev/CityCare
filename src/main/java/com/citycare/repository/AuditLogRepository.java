package com.citycare.repository;

import com.citycare.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserUserId(Long userId);
    List<AuditLog> findByResource(String resource);
}
