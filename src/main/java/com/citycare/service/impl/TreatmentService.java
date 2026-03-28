package com.citycare.service.impl;


import com.citycare.repository.UserRepository;
import com.citycare.dto.request.TreatmentRequest;
import com.citycare.dto.request.TreatmentSummaryResponse;
import com.citycare.entity.Patient;
import com.citycare.entity.Treatment;
import com.citycare.entity.User;
import com.citycare.exception.BadRequestException;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.PatientRepository;
import com.citycare.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public Treatment assignTreatment(Long staffId, TreatmentRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", req.getPatientId()));

        if (patient.getStatus() == Patient.Status.DISCHARGED) {
            throw new BadRequestException(
                    "Cannot assign treatment to patient " + req.getPatientId() +
                            " – they have already been discharged.");
        }

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));

        // Belt-and-suspenders check (SecurityConfig already enforces this via roles)
        if (staff.getRole() != User.Role.DOCTOR && staff.getRole() != User.Role.NURSE) {
            throw new BadRequestException("Only DOCTOR or NURSE can assign treatments");
        }

        Treatment treatment = Treatment.builder()
                .patient(patient)
                .assignedBy(staff)
                .description(req.getDescription())
                .medicationName(req.getMedicationName())
                .dosage(req.getDosage())
                .build(); // status defaults to ONGOING, startDate defaults to today

        return treatmentRepository.save(treatment);
        // Hibernate: INSERT INTO treatments (patient_id, assigned_by, description, ...)
    }

    @Transactional
    public Treatment updateStatus(Long treatmentId, Treatment.Status newStatus) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment", treatmentId));

        treatment.setStatus(newStatus);

        // Record when the treatment ended
        if (newStatus == Treatment.Status.COMPLETED || newStatus == Treatment.Status.CANCELLED) {
            treatment.setEndDate(LocalDate.now());
        }

        return treatmentRepository.save(treatment);
    }

    public List<TreatmentSummaryResponse> getForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            // Idhi Exception throw chesthundhi, appudu automatic ga 404 Error vasthundhi
            throw new ResourceNotFoundException("Patient", patientId);
        }
        List<Treatment> treatments = treatmentRepository.findByPatientPatientId(patientId);

        List<TreatmentSummaryResponse> responseList = new ArrayList<>();

        for (Treatment t : treatments) {

            TreatmentSummaryResponse dto = TreatmentSummaryResponse.builder()
                    .treatmentId(t.getTreatmentId())
                    .patientId(t.getPatient().getPatientId())
                    .patientName(t.getPatient().getCitizen().getName())

                    // Fields from Patient Entity
                    .admissionDate(t.getPatient().getAdmissionDate())
                    .dischargeDate(t.getPatient().getDischargeDate())
                    .ward(t.getPatient().getWard())
                    .notes(t.getPatient().getNotes())
                    .patientStatus(t.getPatient().getStatus().toString())

                    // Field from User Entity (Staff)
                    .assignedByDoctor(t.getAssignedBy().getName())

                    // Fields from Treatment Entity
                    .description(t.getDescription())
                    .medicationName(t.getMedicationName())
                    .dosage(t.getDosage())
                    .startDate(t.getStartDate())
                    .endDate(t.getEndDate())
                    .status(t.getStatus())
                    .build();

            responseList.add(dto);
        }

        return responseList;
    }


    public List<TreatmentSummaryResponse> getMyAssigned(Long staffId) {
        if (!userRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("Staff", staffId);
        }

        List<Treatment> treatments = treatmentRepository.findByAssignedByUserId(staffId);

        List<TreatmentSummaryResponse> responseList = new ArrayList<>();

        for (Treatment t : treatments) {
            // DTO building with all fields
            TreatmentSummaryResponse dto = TreatmentSummaryResponse.builder()
                    .treatmentId(t.getTreatmentId())
                    .patientId(t.getPatient().getPatientId())
                    .patientName(t.getPatient().getCitizen().getName())

                    // Patient Admission Details
                    .admissionDate(t.getPatient().getAdmissionDate())
                    .dischargeDate(t.getPatient().getDischargeDate())
                    .ward(t.getPatient().getWard())
                    .notes(t.getPatient().getNotes())
                    .patientStatus(t.getPatient().getStatus().toString()) //here directly we cant assign the enums directly to string this is one blocker

                    // Staff (Doctor/Nurse) Details
                    .assignedByDoctor(t.getAssignedBy().getName())

                    // Treatment Details
                    .description(t.getDescription())
                    .medicationName(t.getMedicationName())
                    .dosage(t.getDosage())
                    .startDate(t.getStartDate())
                    .endDate(t.getEndDate())
                    .status(t.getStatus())
                    .build();

            responseList.add(dto);
        }

        return responseList;
    }
}
