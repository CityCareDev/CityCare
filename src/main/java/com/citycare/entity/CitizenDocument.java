package com.citycare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "citizen_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitizenDocument extends BaseEntity {

    public enum DocType {
        ID_PROOF, HEALTH_CARD
    }

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocType docType;

    @Column(nullable = false)
    private String fileUri;

    private LocalDate uploadedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
}
