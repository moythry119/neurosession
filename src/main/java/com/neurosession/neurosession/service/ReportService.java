package com.neurosession.neurosession.service;

import com.neurosession.neurosession.dto.ExperimentSessionResponse;
import com.neurosession.neurosession.dto.ParticipantResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final ParticipantService participantService;
    private final ExperimentSessionService sessionService;
    private final WebClient webClient;

    public ReportService(ParticipantService participantService,
                         ExperimentSessionService sessionService) {
        this.participantService = participantService;
        this.sessionService = sessionService;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com")
                .build();
    }

    public String generateReport(Long participantId) {

        ParticipantResponse participant = participantService.getById(participantId);
        List<ExperimentSessionResponse> sessions =
                sessionService.getByParticipant(participantId);

        ExperimentSessionResponse vrh = sessions.stream()
                .filter(s -> "VRH".equals(s.getSessionType()))
                .findFirst().orElse(null);

        ExperimentSessionResponse hyp = sessions.stream()
                .filter(s -> "HYP".equals(s.getSessionType()))
                .findFirst().orElse(null);

        String prompt = buildPrompt(participant, vrh, hyp);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant",
                "max_tokens", 1024,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        Map response = webClient.post()
                .uri("/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Groq uses OpenAI-compatible response format
        List<Map> choices = (List<Map>) response.get("choices");
        Map message = (Map) choices.get(0).get("message");
        return (String) message.get("content");
    }

    private String buildPrompt(ParticipantResponse p,
                               ExperimentSessionResponse vrh,
                               ExperimentSessionResponse hyp) {
        StringBuilder sb = new StringBuilder();

        sb.append("You are a research assistant summarizing participant data ");
        sb.append("from a study comparing Virtual Reality Hypnosis (VRH) and ");
        sb.append("Traditional Hypnosis (HYP). Write in clear, concise scientific language.\n\n");

        sb.append("Generate two sections:\n");
        sb.append("1. PARTICIPANT SUMMARY — describe the participant baseline ");
        sb.append("profile and session outcomes in plain language.\n");
        sb.append("2. VRH vs HYP COMPARISON — highlight key differences between ");
        sb.append("the two conditions for this participant.\n\n");

        sb.append("PARTICIPANT:\n");
        sb.append("Code: ").append(p.getParticipantCode()).append("\n");
        sb.append("Sex: ").append(p.getSex() == 0 ? "Male" : "Female").append("\n");
        sb.append("Age: ").append(p.getAge()).append("\n");
        sb.append("EHS Level: ").append(p.getEhsLevel()).append("\n");
        sb.append("EHS Score: ").append(p.getEhsScore()).append("\n");
        sb.append("QPI Total: ").append(p.getQpiTotal()).append("\n");
        sb.append("STAI-Etat: ").append(p.getStaiEtatTotal()).append("\n");
        sb.append("STAI-Trait: ").append(p.getStaiTraitTotal()).append("\n");
        sb.append("TAS Total: ").append(p.getTasTotal()).append("\n");
        sb.append("DES Total: ").append(p.getDesTotal()).append("\n\n");

        if (vrh != null) {
            sb.append("VRH SESSION:\n");
            sb.append("VAS Anxiety Pre/Post: ")
                    .append(vrh.getVasPreAnxiete()).append(" / ")
                    .append(vrh.getVasPostAnxiete()).append("\n");
            sb.append("VAS Pain Pre/Post: ")
                    .append(vrh.getVasPreDouleur()).append(" / ")
                    .append(vrh.getVasPostDouleur()).append("\n");
            sb.append("Absorption: ").append(vrh.getAbsorption()).append("\n");
            sb.append("Dissociation: ").append(vrh.getDissociation()).append("\n");
            sb.append("Automaticity: ").append(vrh.getAutomaticite()).append("\n");
            sb.append("Vigilance: ").append(vrh.getEveilVigilance()).append("\n");
            sb.append("Time Perception: ")
                    .append(vrh.getPerceptionTempsMinutes()).append(" min\n");
            sb.append("CSQ Nausea: ").append(vrh.getCsqNausea()).append("\n\n");
        } else {
            sb.append("VRH SESSION: not yet recorded\n\n");
        }

        if (hyp != null) {
            sb.append("HYP SESSION:\n");
            sb.append("VAS Anxiety Pre/Post: ")
                    .append(hyp.getVasPreAnxiete()).append(" / ")
                    .append(hyp.getVasPostAnxiete()).append("\n");
            sb.append("VAS Pain Pre/Post: ")
                    .append(hyp.getVasPreDouleur()).append(" / ")
                    .append(hyp.getVasPostDouleur()).append("\n");
            sb.append("Absorption: ").append(hyp.getAbsorption()).append("\n");
            sb.append("Dissociation: ").append(hyp.getDissociation()).append("\n");
            sb.append("Automaticity: ").append(hyp.getAutomaticite()).append("\n");
            sb.append("Vigilance: ").append(hyp.getEveilVigilance()).append("\n");
            sb.append("Time Perception: ")
                    .append(hyp.getPerceptionTempsMinutes()).append(" min\n");
            sb.append("CSQ Nausea: ").append(hyp.getCsqNausea()).append("\n\n");
        } else {
            sb.append("HYP SESSION: not yet recorded\n\n");
        }

        sb.append("Keep the response concise. ");
        sb.append("This is a documentation aid — human review is required.");

        return sb.toString();
    }
}