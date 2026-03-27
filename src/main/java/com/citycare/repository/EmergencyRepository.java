package com.citycare.repository;

import com.citycare.entity.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ============================================================
 * EmergencyRepository.java  –  Database ops for 'emergencies'
 * ============================================================
 *
 * Spring Data JPA reads these method names and auto-generates SQL:
 *
 * findByStatus(REPORTED)
 *   → SELECT * FROM emergencies WHERE status = 'REPORTED'
 *   → Dispatcher polls this to see incoming emergencies.
 *
 * findByStatus(DISPATCHED)
 *   → Admin polls this to see emergencies ready for patient admission.
 *
 * findByCitizenUserId(5L)
 *   → navigates: Emergency → citizen (User) → userId
 *   → SELECT * FROM emergencies WHERE citizen_id = 5
 *   → Citizen views their own history.
 *
 * findByStatusOrderByCreatedAtDesc(REPORTED)
 *   → Same as above but newest first (most urgent at top).
 *
 * ============================================================
 * HOW THIS FILE WORKS:
 *   Called by EmergencyService for all emergency lookup operations.
 * ============================================================
 */
@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {

    List<Emergency> findByStatus(Emergency.Status status);

    // Newest emergencies appear first – important for dispatcher alert list
    List<Emergency> findByStatusOrderByCreatedAtDesc(Emergency.Status status);

    List<Emergency> findByCitizenUserId(Long citizenId);

    List<Emergency> findByDispatcherUserId(Long dispatcherId);
}
