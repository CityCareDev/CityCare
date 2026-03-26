package com.citycare.repository;

import com.citycare.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * TreatmentRepository.java  –  Database ops for 'treatments'
 * ============================================================
 *
 * findByPatientPatientId(2L)
 *   → navigates: Treatment → patient (Patient) → patientId
 *   → SELECT * FROM treatments WHERE patient_id = 2
 *   → Staff views all treatments assigned to a patient.
 *
 * findByAssignedByUserId(4L)
 *   → navigates: Treatment → assignedBy (User) → userId
 *   → SELECT * FROM treatments WHERE assigned_by = 4
 *   → Staff views treatments they personally assigned.
 *
 * ============================================================
 */
@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findByPatientPatientId(Long patientId);

    List<Treatment> findByAssignedByUserId(Long staffId);
}
