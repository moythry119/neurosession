package com.neurosession.neurosession.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "participants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String participantCode;

    @NotNull
    @Column(nullable = false)
    private Integer sex;                   // 0 = Male, 1 = Female

    @NotNull
    @Column(nullable = false)
    private Integer age;

    @Column
    private LocalDateTime timeStarted;

    // Baseline questionnaire scores

    @Column
    private Double qpiTotal;              // QPI-TOT: immersion propensity (Questionnaire sur la Propension à l'Immersion)

    @Column
    private Double staiEtatTotal;         // STAI-ETAT-TOT: state anxiety

    @Column
    private Double staiTraitTotal;        // STAI-TRAIT-TOT: trait anxiety

    @Column
    private Double tasTotal;              // TAS-TOT: absorption (Tellegen Absorption Scale)

    @Column
    private Double desTotal;              // DES-TOT: dissociation

    @Column
    private Double ehsScore;             // EHS raw score: hypnotic susceptibility

    @Column
    private String ehsLevel;             // "Bas", "Moyen", "Elevé" as in low, medium, high

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "researcher_id", nullable = false)
    private User researcher;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<ExperimentSession> sessions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}