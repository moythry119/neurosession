package com.neurosession.neurosession.service;

import com.neurosession.neurosession.dto.ParticipantProgressResponse;
import com.neurosession.neurosession.dto.ParticipantRequest;
import com.neurosession.neurosession.dto.ParticipantResponse;
import com.neurosession.neurosession.entity.ExperimentSession;
import com.neurosession.neurosession.entity.Participant;
import com.neurosession.neurosession.entity.User;
import com.neurosession.neurosession.repository.ParticipantRepository;
import com.neurosession.neurosession.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// Main rule: researchers only ever see their own data
@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public ParticipantResponse create(ParticipantRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User researcher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Researcher not found"));

        if (participantRepository.existsByParticipantCode(request.getParticipantCode())) {
            throw new RuntimeException("Participant code already exists: "
                    + request.getParticipantCode());
        }

        Participant participant = Participant.builder()
                .participantCode(request.getParticipantCode())
                .sex(request.getSex())
                .age(request.getAge())
                .timeStarted(request.getTimeStarted())
                .qpiTotal(request.getQpiTotal())
                .staiEtatTotal(request.getStaiEtatTotal())
                .staiTraitTotal(request.getStaiTraitTotal())
                .tasTotal(request.getTasTotal())
                .desTotal(request.getDesTotal())
                .ehsScore(request.getEhsScore())
                .ehsLevel(request.getEhsLevel())
                .researcher(researcher)
                .build();

        Participant saved = participantRepository.save(participant);
        return toResponse(saved);
    }

    public List<ParticipantResponse> getAll() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User researcher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Researcher not found"));

        return participantRepository.findByResearcherId(researcher.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ParticipantResponse getById(Long id) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found: " + id));
        return toResponse(participant);
    }

    private ParticipantResponse toResponse(Participant p) {
        return ParticipantResponse.builder()
                .id(p.getId())
                .participantCode(p.getParticipantCode())
                .sex(p.getSex())
                .age(p.getAge())
                .timeStarted(p.getTimeStarted())
                .qpiTotal(p.getQpiTotal())
                .staiEtatTotal(p.getStaiEtatTotal())
                .staiTraitTotal(p.getStaiTraitTotal())
                .tasTotal(p.getTasTotal())
                .desTotal(p.getDesTotal())
                .ehsScore(p.getEhsScore())
                .ehsLevel(p.getEhsLevel())
                .createdAt(p.getCreatedAt())
                .researcherEmail(p.getResearcher().getEmail())
                .build();
    }

    public ParticipantResponse update(Long id, ParticipantRequest request) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found: " + id));

        participant.setParticipantCode(request.getParticipantCode());
        participant.setSex(request.getSex());
        participant.setAge(request.getAge());
        participant.setTimeStarted(request.getTimeStarted());
        participant.setQpiTotal(request.getQpiTotal());
        participant.setStaiEtatTotal(request.getStaiEtatTotal());
        participant.setStaiTraitTotal(request.getStaiTraitTotal());
        participant.setTasTotal(request.getTasTotal());
        participant.setDesTotal(request.getDesTotal());
        participant.setEhsScore(request.getEhsScore());
        participant.setEhsLevel(request.getEhsLevel());

        return toResponse(participantRepository.save(participant));
    }

    public void delete(Long id) {
        participantRepository.deleteById(id);
    }

    public List<ParticipantProgressResponse> getProgress() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User researcher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Researcher not found"));

        return participantRepository.findByResearcherId(researcher.getId())
                .stream()
                .map(this::toProgressResponse)
                .collect(Collectors.toList());
    }

    private ParticipantProgressResponse toProgressResponse(Participant p) {
        List<ExperimentSession> sessions = p.getSessions();

        boolean vrhCompleted = sessions != null && sessions.stream()
                .anyMatch(s -> "VRH".equals(s.getSessionType()));
        boolean hypCompleted = sessions != null && sessions.stream()
                .anyMatch(s -> "HYP".equals(s.getSessionType()));

        String status;
        if (vrhCompleted && hypCompleted) {
            status = "Complete";
        } else if (vrhCompleted) {
            status = "Missing HYP";
        } else if (hypCompleted) {
            status = "Missing VRH";
        } else {
            status = "Missing both";
        }

        return ParticipantProgressResponse.builder()
                .id(p.getId())
                .participantCode(p.getParticipantCode())
                .vrhCompleted(vrhCompleted)
                .hypCompleted(hypCompleted)
                .status(status)
                .build();
    }
}