package com.neurosession.neurosession.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "experiment_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String sessionType;            // "VRH" or "HYP"

    // Side effects (CSQ)
    @Column
    private Double csqNausea;             // cybersickness: nausea
    @Column
    private Double csqOculomotor;         // cybersickness: oculomotor

    // Presence scores
    @Column
    private Double presenceSpatiale;      // spatial presence
    @Column
    private Double validiteEcologique;    // ecological validity
    @Column
    private Double presenceSociale;       // social presence
    @Column
    private Double copresence;            // copresence

    // VAS measures (0–10)
    @Column
    private Double vasPreAnxiete;         // pre-session anxiety VAS
    @Column
    private Double vasPostAnxiete;        // post-session anxiety VAS
    @Column
    private Double vasPreDouleur;         // pre-session pain VAS
    @Column
    private Double vasPostDouleur;        // post-session pain VAS

    //  Subjective experience (0–10)
    @Column
    private Double absorption;            // attentional focus
    @Column
    private Double dissociation;          // body/reality disconnection
    @Column
    private Double automaticite;          // automaticity (inverse of control)
    @Column
    private Double eveilVigilance;        // arousal/vigilance

    // Time perception
    @Column
    private Double perceptionTempsMinutes; // subjective time in minutes

    //  Satisfaction (Q1–Q8)
    @Column
    private Double satisfactionQ1;
    @Column
    private Double satisfactionQ2;
    @Column
    private Double satisfactionQ3;
    @Column
    private Double satisfactionQ4;
    @Column
    private Double satisfactionQ5;
    @Column
    private Double satisfactionQ6;
    @Column
    private Double satisfactionQ7;
    @Column
    private Double satisfactionQ8;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}