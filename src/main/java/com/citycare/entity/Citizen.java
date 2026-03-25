package com.citycare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "citizens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Citizen extends BaseEntity {

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long citizenId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    // Link to the User account of this citizen
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
