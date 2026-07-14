package com.neurosession.neurosession.controller;

import com.neurosession.neurosession.dto.ParticipantProgressResponse;
import com.neurosession.neurosession.dto.ParticipantRequest;
import com.neurosession.neurosession.dto.ParticipantResponse;
import com.neurosession.neurosession.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping
    public ResponseEntity<ParticipantResponse> create(
            @Valid @RequestBody ParticipantRequest request) {
        return ResponseEntity.ok(participantService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ParticipantResponse>> getAll() {
        return ResponseEntity.ok(participantService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(participantService.getById(id));
    }

    @GetMapping("/progress")
    public ResponseEntity<List<ParticipantProgressResponse>> getProgress() {
        return ResponseEntity.ok(participantService.getProgress());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParticipantResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantRequest request) {
        return ResponseEntity.ok(participantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}