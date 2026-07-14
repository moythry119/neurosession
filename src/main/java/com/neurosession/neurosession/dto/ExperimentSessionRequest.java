package com.neurosession.neurosession.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExperimentSessionRequest {

    @NotBlank
    private String sessionType;        // "VRH" or "HYP"

    // VAS measures (0-10)
    private Double vasPreAnxiete;
    private Double vasPostAnxiete;
    private Double vasPreDouleur;
    private Double vasPostDouleur;

    // Side effects
    private Double csqNausea;
    private Double csqOculomotor;

    // Presence
    private Double presenceSpatiale;
    private Double validiteEcologique;
    private Double presenceSociale;
    private Double copresence;

    // Subjective experience
    private Double absorption;
    private Double dissociation;
    private Double automaticite;
    private Double eveilVigilance;
    private Double perceptionTempsMinutes;

    // Satisfaction
    private Double satisfactionQ1;
    private Double satisfactionQ2;
    private Double satisfactionQ3;
    private Double satisfactionQ4;
    private Double satisfactionQ5;
    private Double satisfactionQ6;
    private Double satisfactionQ7;
    private Double satisfactionQ8;
}