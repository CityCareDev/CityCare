package com.citycare.service;

import com.citycare.dto.request.AuditRequest;
import com.citycare.dto.request.ComplianceRecordRequest;
import com.citycare.entity.Audit;
import com.citycare.entity.AuditLog;
import com.citycare.entity.ComplianceRecord;
import com.citycare.entity.User;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.AuditLogRepository;
import com.citycare.repository.AuditRepository;
import com.citycare.repository.UserRepository;
import com.citycare.repository.ComplianceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplianceService {

    private final ComplianceRecordRepository complianceRepository;
    private final AuditRepository auditRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    // ── Compliance Records ────────────────────────────────────────────────────

    @Transactional
    public ComplianceRecord createRecord(Long officerId, ComplianceRecordRequest req) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", officerId));

        ComplianceRecord record = ComplianceRecord.builder()
                .entityId(req.getEntityId())
                .type(req.getType())
                .result(req.getResult())
                .date(req.getDate() != null ? req.getDate() : LocalDate.now())
                .notes(req.getNotes())
                .officer(officer)
                .build();

        return complianceRepository.save(record);
    }

    public List<ComplianceRecord> getAllRecords() {

        return complianceRepository.findAll();
    }

    public List<ComplianceRecord> getRecordsByType(ComplianceRecord.EntityType type) {
        return complianceRepository.findByType(type);
    }

    public List<ComplianceRecord> getRecordsByEntity(Long entityId) {
        return complianceRepository.findByEntityId(entityId);
    }

    public ComplianceRecord getRecordById(Long id) {
        return complianceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ComplianceRecord", id));
    }

    // ── Audits ────────────────────────────────────────────────────────────────

    @Transactional
    public Audit createAudit(Long officerId, AuditRequest req) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", officerId));

        Audit audit = Audit.builder()
                .officer(officer)
                .scope(req.getScope())
                .findings(req.getFindings())
                .date(req.getDate())
                .build();

        return auditRepository.save(audit);
    }

    @Transactional
    public Audit updateAuditStatus(Long auditId, Audit.Status status, String findings) {
        Audit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit", auditId));
        audit.setStatus(status);
        if (findings != null) audit.setFindings(findings);
        return auditRepository.save(audit);
    }

    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }

    public List<Audit> getAuditsByOfficer(Long officerId) {

        return auditRepository.findByOfficerUserId(officerId);
    }

    public Audit getAuditById(Long id) {
        return auditRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit", id));
    }

    // ── Audit Logs ────────────────────────────────────────────────────────────

    @Transactional
    public AuditLog logAction(Long userId, String action, String resource) {
        User user = userRepository.findById(userId).orElse(null);
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .resource(resource)
                .timestamp(LocalDateTime.now())
                .build();
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {

        return auditLogRepository.findAll();
    }

    public List<AuditLog> getLogsByUser(Long userId) {

        return auditLogRepository.findByUserUserId(userId);
    }
}
