package com.citycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * ============================================================
 * Ambulance.java  –  Fleet Management
 * ============================================================
 *
 * Represents one physical ambulance vehicle.
 * Only ADMIN can add ambulances (POST /admin/ambulances).
 * Ambulances do NOT self-register.
 *
 * STATUS LIFECYCLE:
 *
 *   AVAILABLE ──── dispatcher dispatches ────► DISPATCHED
 *       ▲                                          │
 *       └──── admin admits patient (AUTO) ─────────┘
 *
 *   AVAILABLE ◄──► MAINTENANCE  (admin can toggle manually)
 *
 * The auto-release happens inside PatientService.admitPatient():
 *   ambulance.setStatus(AVAILABLE)   ← no manual step needed
 *
 * ============================================================
 * TABLE CREATED BY HIBERNATE:
 *   CREATE TABLE ambulances (
 *     ambulance_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     vehicle_number VARCHAR(255) NOT NULL UNIQUE,
 *     model          VARCHAR(255),
 *     status         VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
 *     created_at     DATETIME(6) NOT NULL,
 *     updated_at     DATETIME(6)
 *   );
 * ============================================================
 */
@Entity
@Table(name = "ambulances",
        uniqueConstraints = @UniqueConstraint(columnNames = "vehicle_number"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ambulance extends BaseEntity {

    public enum Status {
        AVAILABLE,    // Ready to be dispatched to an emergency
        DISPATCHED,   // Currently en route to an emergency
        MAINTENANCE   // Under repair – not available for dispatch
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ambulanceId;

    @NotBlank
    @Column(name = "vehicle_number", nullable = false, unique = true)
    private String vehicleNumber; // e.g. "TN-01-AB-1234"

    private String model; // e.g. "Toyota HiAce Advanced Life Support"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.AVAILABLE; // Always starts AVAILABLE when added
}
