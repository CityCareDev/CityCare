package com.citycare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "compliance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceRecord extends BaseEntity {

    public enum EntityType {
        FACILITY, PATIENT, EMERGENCY
    }

    public enum Result {
        COMPLIANT, NON_COMPLIANT, UNDER_REVIEW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complianceId;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id")
    private User officer;
}
