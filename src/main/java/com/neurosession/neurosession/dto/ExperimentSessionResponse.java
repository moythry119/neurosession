package com.neurosession.neurosession.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentSessionResponse {

    private Long id;
    private String sessionType;
    private Long participantId;
    private String participantCode;

    private Double vasPreAnxiete;
    private Double vasPostAnxiete;
    private Double vasPreDouleur;
    private Double vasPostDouleur;

    private Double csqNausea;
    private Double csqOculomotor;

    private Double presenceSpatiale;
    private Double validiteEcologique;
    private Double presenceSociale;
    private Double copresence;

    private Double absorption;
    private Double dissociation;
    private Double automaticite;
    private Double eveilVigilance;
    private Double perceptionTempsMinutes;

    private Double satisfactionQ1;
    private Double satisfactionQ2;
    private Double satisfactionQ3;
    private Double satisfactionQ4;
    private Double satisfactionQ5;
    private Double satisfactionQ6;
    private Double satisfactionQ7;
    private Double satisfactionQ8;

    private LocalDateTime createdAt;
}