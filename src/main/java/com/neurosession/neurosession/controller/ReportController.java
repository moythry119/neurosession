package com.neurosession.neurosession.controller;

import com.neurosession.neurosession.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // generates clinical summary + VRH vs HYP comparison for one participant
    @GetMapping("/participants/{id}/report")
    public ResponseEntity<Map<String, String>> generateReport(
            @PathVariable Long id) {
        String report = reportService.generateReport(id);
        return ResponseEntity.ok(Map.of("report", report));
    }
}