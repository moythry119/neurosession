package com.neurosession.neurosession.service;

import com.neurosession.neurosession.dto.ExperimentSessionRequest;
import com.neurosession.neurosession.dto.ExperimentSessionResponse;
import com.neurosession.neurosession.entity.ExperimentSession;
import com.neurosession.neurosession.entity.Participant;
import com.neurosession.neurosession.repository.ExperimentSessionRepository;
import com.neurosession.neurosession.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentSessionService {

    private final ExperimentSessionRepository sessionRepository;
    private final ParticipantRepository participantRepository;

    public ExperimentSessionResponse create(Long participantId,
                                            ExperimentSessionRequest request) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found: " + participantId));

        ExperimentSession session = ExperimentSession.builder()
                .sessionType(request.getSessionType())
                .vasPreAnxiete(request.getVasPreAnxiete())
                .vasPostAnxiete(request.getVasPostAnxiete())
                .vasPreDouleur(request.getVasPreDouleur())
                .vasPostDouleur(request.getVasPostDouleur())
                .csqNausea(request.getCsqNausea())
                .csqOculomotor(request.getCsqOculomotor())
                .presenceSpatiale(request.getPresenceSpatiale())
                .validiteEcologique(request.getValiditeEcologique())
                .presenceSociale(request.getPresenceSociale())
                .copresence(request.getCopresence())
                .absorption(request.getAbsorption())
                .dissociation(request.getDissociation())
                .automaticite(request.getAutomaticite())
                .eveilVigilance(request.getEveilVigilance())
                .perceptionTempsMinutes(request.getPerceptionTempsMinutes())
                .satisfactionQ1(request.getSatisfactionQ1())
                .satisfactionQ2(request.getSatisfactionQ2())
                .satisfactionQ3(request.getSatisfactionQ3())
                .satisfactionQ4(request.getSatisfactionQ4())
                .satisfactionQ5(request.getSatisfactionQ5())
                .satisfactionQ6(request.getSatisfactionQ6())
                .satisfactionQ7(request.getSatisfactionQ7())
                .satisfactionQ8(request.getSatisfactionQ8())
                .participant(participant)
                .build();

        return toResponse(sessionRepository.save(session));
    }

    public ExperimentSessionResponse update(Long sessionId,
                                            ExperimentSessionRequest request) {
        ExperimentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.setVasPreAnxiete(request.getVasPreAnxiete());
        session.setVasPostAnxiete(request.getVasPostAnxiete());
        session.setVasPreDouleur(request.getVasPreDouleur());
        session.setVasPostDouleur(request.getVasPostDouleur());
        session.setCsqNausea(request.getCsqNausea());
        session.setCsqOculomotor(request.getCsqOculomotor());
        session.setPresenceSpatiale(request.getPresenceSpatiale());
        session.setValiditeEcologique(request.getValiditeEcologique());
        session.setPresenceSociale(request.getPresenceSociale());
        session.setCopresence(request.getCopresence());
        session.setAbsorption(request.getAbsorption());
        session.setDissociation(request.getDissociation());
        session.setAutomaticite(request.getAutomaticite());
        session.setEveilVigilance(request.getEveilVigilance());
        session.setPerceptionTempsMinutes(request.getPerceptionTempsMinutes());
        session.setSatisfactionQ1(request.getSatisfactionQ1());
        session.setSatisfactionQ2(request.getSatisfactionQ2());
        session.setSatisfactionQ3(request.getSatisfactionQ3());
        session.setSatisfactionQ4(request.getSatisfactionQ4());
        session.setSatisfactionQ5(request.getSatisfactionQ5());
        session.setSatisfactionQ6(request.getSatisfactionQ6());
        session.setSatisfactionQ7(request.getSatisfactionQ7());
        session.setSatisfactionQ8(request.getSatisfactionQ8());

        return toResponse(sessionRepository.save(session));
    }

    public List<ExperimentSessionResponse> getByParticipant(Long participantId) {
        return sessionRepository.findByParticipantId(participantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void delete(Long sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    private ExperimentSessionResponse toResponse(ExperimentSession s) {
        return ExperimentSessionResponse.builder()
                .id(s.getId())
                .sessionType(s.getSessionType())
                .participantId(s.getParticipant().getId())
                .participantCode(s.getParticipant().getParticipantCode())
                .vasPreAnxiete(s.getVasPreAnxiete())
                .vasPostAnxiete(s.getVasPostAnxiete())
                .vasPreDouleur(s.getVasPreDouleur())
                .vasPostDouleur(s.getVasPostDouleur())
                .csqNausea(s.getCsqNausea())
                .csqOculomotor(s.getCsqOculomotor())
                .presenceSpatiale(s.getPresenceSpatiale())
                .validiteEcologique(s.getValiditeEcologique())
                .presenceSociale(s.getPresenceSociale())
                .copresence(s.getCopresence())
                .absorption(s.getAbsorption())
                .dissociation(s.getDissociation())
                .automaticite(s.getAutomaticite())
                .eveilVigilance(s.getEveilVigilance())
                .perceptionTempsMinutes(s.getPerceptionTempsMinutes())
                .satisfactionQ1(s.getSatisfactionQ1())
                .satisfactionQ2(s.getSatisfactionQ2())
                .satisfactionQ3(s.getSatisfactionQ3())
                .satisfactionQ4(s.getSatisfactionQ4())
                .satisfactionQ5(s.getSatisfactionQ5())
                .satisfactionQ6(s.getSatisfactionQ6())
                .satisfactionQ7(s.getSatisfactionQ7())
                .satisfactionQ8(s.getSatisfactionQ8())
                .createdAt(s.getCreatedAt())
                .build();
    }
}