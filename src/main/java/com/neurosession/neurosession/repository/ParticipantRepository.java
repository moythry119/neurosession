package com.neurosession.neurosession.repository;

import com.neurosession.neurosession.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByParticipantCode(String participantCode);

    boolean existsByParticipantCode(String participantCode);

    List<Participant> findByResearcherId(Long researcherId);
}