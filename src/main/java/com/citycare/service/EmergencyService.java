package com.citycare.service.impl;

import com.citycare.dto.request.DispatchRequest;
import com.citycare.dto.request.EmergencyRequest;
import com.citycare.entity.Ambulance;
import com.citycare.entity.Emergency;
import com.citycare.entity.User;
import com.citycare.exception.BadRequestException;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.AmbulanceRepository;
import com.citycare.repository.EmergencyRepository;
import com.citycare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================
 * EmergencyService.java  –  Emergency Reporting + Dispatch Logic
 * ============================================================
 *
 * STEP 1 – reportEmergency() (called by CITIZEN):
 *   - Validates the citizen exists
 *   - Creates Emergency with status = REPORTED
 *   - Dispatcher dashboard polls GET /emergencies/pending to see this
 *
 * STEP 2 – dispatchAmbulance() (called by DISPATCHER):
 *   - Validates emergency exists and is still REPORTED (not already handled)
 *   - Validates ambulance exists and is AVAILABLE
 *     (if not → throws BadRequestException with clear message)
 *   - Sets ambulance.status = DISPATCHED (no longer available)
 *   - Sets emergency.status = DISPATCHED
 *   - Records: which dispatcher, which ambulance, what time
 *   - Admin dashboard polls GET /emergencies/dispatched to see this
 *
 * @Transactional on dispatchAmbulance():
 *   Ambulance update + Emergency update happen as ONE DB transaction.
 *   If ambulance save succeeds but emergency save fails (or vice versa),
 *   BOTH changes are rolled back → no inconsistent state in the DB.
 *
 * ============================================================
 * HOW THIS FILE WORKS:
 *   Called by EmergencyController. All DB access goes through
 *   EmergencyRepository and AmbulanceRepository.
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final UserRepository userRepository;

    // ── STEP 1: Citizen reports emergency ─────────────────────────────────────

    @Transactional
    public Emergency reportEmergency(Long citizenId, EmergencyRequest req) {
        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() -> new ResourceNotFoundException("User", citizenId));

        Emergency emergency = Emergency.builder()
                .citizen(citizen)
                .type(req.getType())
                .location(req.getLocation())
                .description(req.getDescription())
                .status(Emergency.Status.REPORTED)
                .build();

        return emergencyRepository.save(emergency);
        // Hibernate: INSERT INTO emergencies (citizen_id, type, location, status, ...)
    }

    // ── STEP 2a: Dispatcher views available ambulances ────────────────────────

    public List<Ambulance> getAvailableAmbulances() {
        // SQL: SELECT * FROM ambulances WHERE status = 'AVAILABLE'
        return ambulanceRepository.findByStatus(Ambulance.Status.AVAILABLE);
    }

    // ── STEP 2b: Dispatcher assigns ambulance ─────────────────────────────────

    @Transactional
    public Emergency dispatchAmbulance(Long emergencyId, Long dispatcherId, DispatchRequest req) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency", emergencyId));

        // Prevent double-dispatching the same emergency
        if (emergency.getStatus() != Emergency.Status.REPORTED) {
            throw new BadRequestException(
                    "Cannot dispatch – emergency is already " + emergency.getStatus() +
                            ". Only REPORTED emergencies can be dispatched.");
        }

        Ambulance ambulance = ambulanceRepository.findById(req.getAmbulanceId())
                .orElseThrow(() -> new ResourceNotFoundException("Ambulance", req.getAmbulanceId()));

        // Key business rule: ambulance must be AVAILABLE
        if (ambulance.getStatus() != Ambulance.Status.AVAILABLE) {
            throw new BadRequestException(
                    "Ambulance " + ambulance.getVehicleNumber() +
                            " is not available (current status: " + ambulance.getStatus() + ")");
        }

        User dispatcher = userRepository.findById(dispatcherId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatcher", dispatcherId));

        // Mark ambulance as busy – it will be released back by PatientService.admitPatient()
        ambulance.setStatus(Ambulance.Status.DISPATCHED);
        ambulanceRepository.save(ambulance); // UPDATE ambulances SET status='DISPATCHED'

        // Record the dispatch on the emergency
        emergency.setStatus(Emergency.Status.DISPATCHED);
        emergency.setDispatcher(dispatcher);
        emergency.setAmbulance(ambulance);
        emergency.setDispatchedAt(LocalDateTime.now());

        return emergencyRepository.save(emergency); // UPDATE emergencies SET status='DISPATCHED'
    }

    // ── Query methods ──────────────────────────────────────────────────────────

    // Dispatcher polls this – shows newest first
    public List<Emergency> getReportedEmergencies() {
        return emergencyRepository.findByStatusOrderByCreatedAtDesc(Emergency.Status.REPORTED);
    }

    // Admin polls this to see which emergencies need patient admission
    public List<Emergency> getDispatchedEmergencies() {
        return emergencyRepository.findByStatus(Emergency.Status.DISPATCHED);
    }

    public Emergency getById(Long id) {
        return emergencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency", id));
    }

    // Citizen views their own history
    public List<Emergency> getMyCases(Long citizenId) {
        return emergencyRepository.findByCitizenUserId(citizenId);
    }
}
