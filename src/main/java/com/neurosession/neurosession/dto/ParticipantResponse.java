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
public class ParticipantResponse {

    private Long id;
    private String participantCode;
    private Integer sex;
    private Integer age;
    private LocalDateTime timeStarted;
    private Double qpiTotal;
    private Double staiEtatTotal;
    private Double staiTraitTotal;
    private Double tasTotal;
    private Double desTotal;
    private Double ehsScore;
    private String ehsLevel;
    private LocalDateTime createdAt;
    private String researcherEmail;
}