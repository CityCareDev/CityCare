package com.citycare.repository;

import com.citycare.entity.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * AmbulanceRepository.java  –  Database ops for 'ambulances'
 * ============================================================
 *
 * findByStatus(Ambulance.Status.AVAILABLE)
 *   → SELECT * FROM ambulances WHERE status = 'AVAILABLE'
 *   → Used by dispatcher to see which vehicles can be assigned.
 *
 * existsByVehicleNumber("TN-01-AB-1234")
 *   → SELECT EXISTS(...) WHERE vehicle_number = 'TN-01-AB-1234'
 *   → Used by admin when adding a new ambulance to prevent duplicates.
 *
 * ============================================================
 * HOW THIS FILE WORKS:
 *   Called by EmergencyService (to list available ambulances)
 *   and AdminService (to add ambulances and check duplicates).
 * ============================================================
 */
@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {

    List<Ambulance> findByStatus(Ambulance.Status status);

    boolean existsByVehicleNumber(String vehicleNumber);
}
