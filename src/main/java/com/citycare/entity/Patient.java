package com.citycare.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    public enum Status {
        ADMITTED, UNDER_OBSERVATION, STABLE, DISCHARGED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User citizen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Emergency emergency;

    @Column(nullable = false)
    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private String ward;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ADMITTED;


    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", citizenId=" + (citizen != null ? citizen.getUserId() : "null") +
                ", emergencyId=" + (emergency != null ? emergency.getEmergencyId() : "null") +
                ", admissionDate=" + admissionDate +
                ", dischargeDate=" + dischargeDate +
                ", ward='" + ward + '\'' +
                ", status=" + status +
                ", notes='" + notes + '\'' +
                '}';
    }
}
