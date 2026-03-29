package com.cts.CityCare.CityCare.service;


import com.cts.CityCare.CityCare.dto.request.FacilityRequest;
import com.cts.CityCare.CityCare.entity.Facility;
import com.cts.CityCare.CityCare.entity.Staff;
import com.cts.CityCare.CityCare.exception.ResourceNotFoundException;
import com.cts.CityCare.CityCare.repository.FacilityRepository;
import com.cts.CityCare.CityCare.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final StaffRepository staffRepository;

    @Transactional
    public Facility createFacility(FacilityRequest req) {
        Facility facility = Facility.builder()   //we use builder because it give us proper naming of parameters as compare to constructor
                .name(req.getName())
                .type(req.getType())
                .location(req.getLocation())
                .capacity(req.getCapacity())
                .status(req.getStatus() != null ? req.getStatus() : Facility.Status.ACTIVE)
                .build();
        return facilityRepository.save(facility);
    }


    @Transactional
    public Facility updateFacility(Long id, FacilityRequest req){
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
        facility.setName(req.getName());
        facility.setType(req.getType());
        facility.setLocation(req.getLocation());
        facility.setCapacity(req.getCapacity());
        if(req.getStatus() != null) facility.setStatus(req.getStatus());
        return facilityRepository.save(facility);
    }



    public Facility getById(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
    }


    public List<Facility> getAll() {
        return facilityRepository.findAll();
    }


    public List<Facility> getByStatus(Facility.Status status) {
        return facilityRepository.findByStatus(status);
    }

    public List<Facility> getByType(Facility.Type type) {
        return facilityRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<Staff> getStaffByFacility(Long facilityId) {
        facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", facilityId));
        return staffRepository.findByFacilityFacilityId(facilityId);
    }

    @Transactional
    public Facility updateStatus(Long id, Facility.Status status) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility", id));
        facility.setStatus(status);
        return facilityRepository.save(facility);
    }
}