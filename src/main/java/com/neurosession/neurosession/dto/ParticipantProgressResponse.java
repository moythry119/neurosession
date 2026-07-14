package com.neurosession.neurosession.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantProgressResponse {

    private Long id;
    private String participantCode;
    private boolean vrhCompleted;
    private boolean hypCompleted;
    private String status; // Complete, Missing VRH, Missing HYP, Missing both
}
