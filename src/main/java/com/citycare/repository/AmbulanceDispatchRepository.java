package com.citycare.repository;

import com.citycare.entity.AmbulanceDispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbulanceDispatchRepository extends JpaRepository<AmbulanceDispatch, Long> {
    List<AmbulanceDispatch> findByEmergencyEmergencyId(Long emergencyId);
    List<AmbulanceDispatch> findByDispatcherUserId(Long dispatcherId);
}
