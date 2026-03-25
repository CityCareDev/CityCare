package com.citycare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audit extends BaseEntity {

    public enum Status {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User officer;

    @Column(nullable = false)
    private String scope;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.SCHEDULED;
}
