package com.citycare.repository;

import com.citycare.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    Optional<Citizen> findByUserUserId(Long userId);
    List<Citizen> findByStatus(Citizen.Status status);
}
