package com.neurosession.neurosession.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipantRequest {

    @NotBlank
    private String participantCode;

    @NotNull
    private Integer sex;               // 0 = Male, 1 = Female

    @NotNull
    private Integer age;

    private LocalDateTime timeStarted;

    private Double qpiTotal;
    private Double staiEtatTotal;
    private Double staiTraitTotal;
    private Double tasTotal;
    private Double desTotal;
    private Double ehsScore;
    private String ehsLevel;
}