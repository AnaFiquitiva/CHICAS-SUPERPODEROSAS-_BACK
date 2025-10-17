package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.ReassignmentReportRequest;
import eci.edu.dosw.proyecto.dto.ReassignmentReportResponse;
import eci.edu.dosw.proyecto.service.interfaces.ReassignmentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReassignmentReportController {

    private final ReassignmentReportService reportService;

    @PostMapping("/reassignment")
    public ResponseEntity<ReassignmentReportResponse> generateReport(@RequestBody ReassignmentReportRequest request) {
        return ResponseEntity.ok(reportService.generateReassignmentReport(request));
    }

    @GetMapping("/reassignment/current-period")
    public ResponseEntity<ReassignmentReportResponse> getCurrentPeriodReport() {
        return ResponseEntity.ok(reportService.generateCurrentPeriodReport());
    }
}