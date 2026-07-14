package com.neurosession.neurosession.controller;

import com.neurosession.neurosession.dto.ExperimentSessionRequest;
import com.neurosession.neurosession.dto.ExperimentSessionResponse;
import com.neurosession.neurosession.service.ExperimentSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExperimentSessionController {

    private final ExperimentSessionService sessionService;

    @PostMapping("/participants/{participantId}/sessions")
    public ResponseEntity<ExperimentSessionResponse> create(
            @PathVariable Long participantId,
            @Valid @RequestBody ExperimentSessionRequest request) {
        return ResponseEntity.ok(sessionService.create(participantId, request));
    }

    @GetMapping("/participants/{participantId}/sessions")
    public ResponseEntity<List<ExperimentSessionResponse>> getByParticipant(
            @PathVariable Long participantId) {
        return ResponseEntity.ok(sessionService.getByParticipant(participantId));
    }

    @PutMapping("/sessions/{sessionId}")
    public ResponseEntity<ExperimentSessionResponse> update(
            @PathVariable Long sessionId,
            @Valid @RequestBody ExperimentSessionRequest request) {
        return ResponseEntity.ok(sessionService.update(sessionId, request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable Long sessionId) {
        sessionService.delete(sessionId);
        return ResponseEntity.noContent().build();
    }
}