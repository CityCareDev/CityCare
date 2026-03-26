package com.citycare.repository;

import com.citycare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * PatientRepository.java  –  Database ops for 'patients'
 * ============================================================
 *
 * findByEmergencyEmergencyId(1L)
 *   → navigates: Patient → emergency (Emergency) → emergencyId
 *   → SELECT * FROM patients WHERE emergency_id = 1
 *   → Used before admission to check: has this emergency already
 *     been admitted as a patient? (prevents double-admission)
 *
 * findByCitizenUserId(3L)
 *   → SELECT * FROM patients WHERE citizen_id = 3
 *   → Used by citizen/staff to view patient history.
 *
 * ============================================================
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmergencyEmergencyId(Long emergencyId);

    List<Patient> findByCitizenUserId(Long citizenId);

    List<Patient> findByStatus(Patient.Status status);
}
