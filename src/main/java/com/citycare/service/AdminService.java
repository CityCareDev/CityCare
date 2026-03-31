package com.citycare.service;

import com.citycare.dto.request.AmbulanceRequest;
import com.citycare.dto.request.CreateStaffRequest;
import com.citycare.entity.Ambulance;
import com.citycare.entity.Facility;
import com.citycare.entity.Staff; // Added Import
import com.citycare.entity.User;
import com.citycare.exception.BadRequestException;
import com.citycare.exception.ResourceNotFoundException;
import com.citycare.repository.AmbulanceRepository;
import com.citycare.repository.FacilityRepository;
import com.citycare.repository.StaffRepository; // Added Import
import com.citycare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AmbulanceRepository ambulanceRepository;
    private final FacilityRepository facilityRepository;
    private final StaffRepository staffRepository; // Injected StaffRepository
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createStaff(CreateStaffRequest req) {
        if (req.getRole() != User.Role.DOCTOR && req.getRole() != User.Role.NURSE) {
            throw new BadRequestException(
                    "Invalid role for staff: " + req.getRole() + ". Use DOCTOR or NURSE.");
        }
        return createAccount(req);
    }

    @Transactional
    public User createDispatcher(CreateStaffRequest req) {
        if (req.getRole() != User.Role.DISPATCHER) {
            throw new BadRequestException("Role must be DISPATCHER for dispatcher accounts");
        }
        return createAccount(req);
    }

    @Transactional
    public User createComplianceOfficer(CreateStaffRequest req) {
        if (req.getRole() != User.Role.COMPLIANCE_OFFICER) {
            throw new BadRequestException("Role must be COMPLIANCE_OFFICER");
        }
        return createAccount(req);
    }

    @Transactional
    public User createCityHealthOfficer(CreateStaffRequest req) {
        if (req.getRole() != User.Role.CITY_HEALTH_OFFICER) {
            throw new BadRequestException("Role must be CITY_HEALTH_OFFICER");
        }
        return createAccount(req);
    }

    private User createAccount(CreateStaffRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already in use: " + req.getEmail());
        }

        Facility facility = null;
        if (req.getFacilityId() != null) {
            facility = facilityRepository.findById(req.getFacilityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Facility", req.getFacilityId()));
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .phone(req.getPhone())
                .facility(facility)
                .build();

        User savedUser = userRepository.save(user);

        // Logic to populate the Staff table if the role is DOCTOR or NURSE
        if (req.getRole() == User.Role.DOCTOR || req.getRole() == User.Role.NURSE) {
            // Your Staff entity requires a facility (nullable = false), so we check it here
            if (facility == null) {
                throw new BadRequestException("Facility ID is mandatory for Doctor or Nurse roles.");
            }

            Staff staff = Staff.builder()
                    .name(savedUser.getName())
                    .role(Staff.Role.valueOf(savedUser.getRole().name()))
                    .contactInfo(savedUser.getPhone())
                    .user(savedUser) // Link to the saved User
                    .facility(facility)
                    .status(Staff.Status.ACTIVE)
                    .build();

            staffRepository.save(staff);
        }

        return savedUser;
    }

    @Transactional
    public Ambulance addAmbulance(AmbulanceRequest req) {
        if (ambulanceRepository.existsByVehicleNumber(req.getVehicleNumber())) {
            throw new BadRequestException(
                    "Ambulance with vehicle number " + req.getVehicleNumber() + " already exists");
        }
        Ambulance ambulance = Ambulance.builder()
                .vehicleNumber(req.getVehicleNumber())
                .model(req.getModel())
                .build();
        return ambulanceRepository.save(ambulance);
    }

    @Transactional
    public Ambulance updateAmbulanceStatus(Long id, Ambulance.Status newStatus) {
        Ambulance ambulance = ambulanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ambulance", id));
        ambulance.setStatus(newStatus);
        return ambulanceRepository.save(ambulance);
    }

    @Transactional
    public User deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setStatus(User.Status.INACTIVE);
        return userRepository.save(user);
    }

    @Transactional
    public User activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setStatus(User.Status.ACTIVE);
        return userRepository.save(user);
    }

    public List<User> getAllStaff() {
        List<User> all = new ArrayList<>(userRepository.findByRole(User.Role.DOCTOR));
        all.addAll(userRepository.findByRole(User.Role.NURSE));
        return all;
    }

    public List<User> getAllDispatchers() {
        return userRepository.findByRole(User.Role.DISPATCHER);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public List<Ambulance> getAllAmbulances() {
        return ambulanceRepository.findAll();
    }
}