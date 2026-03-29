package com.cts.CityCare.CityCare.repository;

import com.cts.CityCare.CityCare.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByFacilityFacilityId(Long facilityId);
    List<Staff> findByRole(Staff.Role role);
    List<Staff> findByStatus(Staff.Status status);
}
