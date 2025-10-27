package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la generación y consulta de reportes estadísticos.
 *
 * Funcionalidades: Reportes y Estadísticas
 * - Grupos más solicitados
 * - Tasa de aprobación vs rechazos
 * - Estadísticas de reasignación
 * - Consulta de casos excepcionales
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Generación y consulta de reportes estadísticos")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Generar reporte")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ReportResponse> generateReport(@Valid @RequestBody ReportRequest reportRequest) {
        ReportResponse report = reportService.generateReport(reportRequest);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener reporte por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable String id) {
        ReportResponse report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Listar reportes por usuario")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ReportResponse>> getReportsByUser(@PathVariable String userId) {
        List<ReportResponse> reports = reportService.getReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Listar reportes por facultad")
    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ReportResponse>> getReportsByFaculty(@PathVariable String facultyId) {
        List<ReportResponse> reports = reportService.getReportsByFaculty(facultyId);
        return ResponseEntity.ok(reports);
    }

    // === REPORTES ESPECÍFICOS ===

    @Operation(summary = "Obtener estadísticas de aprobación")
    @PostMapping("/approval-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ApprovalStatsResponse> getApprovalStatistics(@Valid @RequestBody ReportRequest reportRequest) {
        ApprovalStatsResponse stats = reportService.getApprovalStatistics(reportRequest);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Obtener grupos más solicitados")
    @PostMapping("/most-requested-groups")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<GroupDemandResponse>> getMostRequestedGroups(@Valid @RequestBody ReportRequest reportRequest) {
        List<GroupDemandResponse> groups = reportService.getMostRequestedGroups(reportRequest);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Obtener estadísticas de reasignación")
    @PostMapping("/reassignment-stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ReassignmentStatsResponse> getReassignmentStatistics(@Valid @RequestBody ReportRequest reportRequest) {
        ReassignmentStatsResponse stats = reportService.getReassignmentStatistics(reportRequest);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Obtener reporte de ocupación de grupos")
    @PostMapping("/occupancy-report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<OccupancyReportResponse> getGroupOccupancyReport(@Valid @RequestBody ReportRequest reportRequest) {
        OccupancyReportResponse report = reportService.getGroupOccupancyReport(reportRequest);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Obtener casos excepcionales")
    @PostMapping("/special-approval-cases")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpecialApprovalCaseResponse>> getSpecialApprovalCases(@Valid @RequestBody ReportRequest reportRequest) {
        List<SpecialApprovalCaseResponse> cases = reportService.getSpecialApprovalCases(reportRequest);
        return ResponseEntity.ok(cases);
    }

    // === ESTADÍSTICAS EN TIEMPO REAL ===

    @Operation(summary = "Obtener estadísticas en tiempo real")
    @GetMapping("/real-time/{statType}/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<RealTimeStatsResponse> getRealTimeStatistics(
            @PathVariable String statType,
            @PathVariable String facultyId) {
        RealTimeStatsResponse stats = reportService.getRealTimeStatistics(statType, facultyId);
        return ResponseEntity.ok(stats);
    }

    // === EXPORTACIÓN DE REPORTES ===

    @Operation(summary = "Exportar reporte a PDF")
    @GetMapping("/{reportId}/export/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<String> exportReportToPdf(@PathVariable String reportId) {
        String filePath = reportService.exportReportToPdf(reportId);
        return ResponseEntity.ok(filePath);
    }

    @Operation(summary = "Exportar reporte a Excel")
    @GetMapping("/{reportId}/export/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<String> exportReportToExcel(@PathVariable String reportId) {
        String filePath = reportService.exportReportToExcel(reportId);
        return ResponseEntity.ok(filePath);
    }
}