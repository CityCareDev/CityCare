package com.cts.CityCare.CityCare.service;



import com.cts.CityCare.CityCare.dto.request.CitizenProfileRequest;
import com.cts.CityCare.CityCare.entity.Citizen;
import com.cts.CityCare.CityCare.entity.CitizenDocument;
import com.cts.CityCare.CityCare.entity.User;
import com.cts.CityCare.CityCare.exception.ResourceNotFoundException;
import com.cts.CityCare.CityCare.repository.CitizenDocumentRepository;
import com.cts.CityCare.CityCare.repository.CitizenRepository;
import com.cts.CityCare.CityCare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenDocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Citizen createOrUpdateProfile(Long userId, CitizenProfileRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Citizen existingCitizen = citizenRepository.findByUserUserId(userId)
                .orElse(null);

        Citizen citizen = Citizen.builder()
                .citizenId(existingCitizen != null ? existingCitizen.getCitizenId() : null)
                .user(user)
                .name(req.getName())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .address(req.getAddress())
                .contactInfo(req.getContactInfo())
                .build();

        user.setName(req.getName());
        userRepository.save(user);

        return citizenRepository.save(citizen);
    }

    public Citizen getProfile(Long userId) {
        return citizenRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen profile not found for user " + userId));
    }

    public Citizen getById(Long citizenId) {
        return citizenRepository.findById(citizenId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", citizenId));
    }

    public List<Citizen> getAll() {
        return citizenRepository.findAll();
    }

    @Transactional
    public CitizenDocument uploadDocument(Long citizenId, byte[] documentData) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new ResourceNotFoundException("Citizen", citizenId));

        CitizenDocument doc = CitizenDocument.builder()
                .citizen(citizen)
                .documentData(documentData)
                .uploadedDate(LocalDate.now())
                .verificationStatus(CitizenDocument.VerificationStatus.PENDING)
                .build();

        return documentRepository.save(doc);
    }

    @Transactional
    public CitizenDocument verifyDocument(Long documentId, CitizenDocument.VerificationStatus status) {
        CitizenDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));
        doc.setVerificationStatus(status);
        return documentRepository.save(doc);
    }

    public List<CitizenDocument> getDocuments(Long citizenId) {
        if (!citizenRepository.existsById(citizenId)) {
            throw new ResourceNotFoundException("Citizen not found: " + citizenId);
        }
        return documentRepository.findByCitizenCitizenId(citizenId);

    }
}
