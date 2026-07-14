package com.neurosession.neurosession.repository;

import com.neurosession.neurosession.entity.ExperimentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperimentSessionRepository extends JpaRepository<ExperimentSession, Long> {

    // to get both sessions (VRH + HYP) by participant's Id
    List<ExperimentSession> findByParticipantId(Long participantId);

    // get either VRH or HYP session for a participant
    Optional<ExperimentSession> findByParticipantIdAndSessionType(
            Long participantId, String sessionType
    );

    // get all sessions of a given type for all participants
    List<ExperimentSession> findBySessionType(String sessionType);
}