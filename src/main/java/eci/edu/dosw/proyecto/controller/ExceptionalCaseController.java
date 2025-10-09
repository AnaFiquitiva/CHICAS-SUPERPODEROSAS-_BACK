package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.ExceptionalCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/exceptional-cases")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExceptionalCaseController {

    private final ExceptionalCaseService exceptionalCaseService;

    // ========== ENDPOINTS PARA ESTUDIANTES ==========

    /**
     * Crear un caso excepcional
     */
    @PostMapping("/students/{studentId}")
    public ExceptionalCaseResponseDTO createCase(
            @PathVariable String studentId,
            @Valid @RequestBody ExceptionalCaseRequestDTO requestDTO) {

        log.info("Estudiante {} creando caso excepcional: {}", studentId, requestDTO.getTitle());
        return exceptionalCaseService.createCase(studentId, requestDTO);
    }

    /**
     * Obtener todos los casos del estudiante
     */
    @GetMapping("/students/{studentId}")
    public List<ExceptionalCaseResponseDTO> getStudentCases(@PathVariable String studentId) {
        log.info("Obteniendo casos excepcionales del estudiante: {}", studentId);
        return exceptionalCaseService.getStudentCases(studentId);
    }

    /**
     * Obtener casos activos del estudiante
     */
    @GetMapping("/students/{studentId}/active")
    public List<ExceptionalCaseResponseDTO> getStudentActiveCases(@PathVariable String studentId) {
        log.info("Obteniendo casos activos del estudiante: {}", studentId);
        return exceptionalCaseService.getStudentCases(studentId).stream()
                .filter(caseDTO -> "PENDING".equals(caseDTO.getStatus()) ||
                        "UNDER_REVIEW".equals(caseDTO.getStatus()) ||
                        "NEEDS_INFO".equals(caseDTO.getStatus()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Obtener un caso específico del estudiante
     */
    @GetMapping("/students/{studentId}/cases/{caseId}")
    public ExceptionalCaseResponseDTO getStudentCaseById(
            @PathVariable String studentId,
            @PathVariable String caseId) {

        log.info("Estudiante {} obteniendo caso: {}", studentId, caseId);
        return exceptionalCaseService.getStudentCaseById(studentId, caseId);
    }

    /**
     * Cancelar un caso
     */
    @PutMapping("/students/{studentId}/cases/{caseId}/cancel")
    public ExceptionalCaseResponseDTO cancelCase(
            @PathVariable String studentId,
            @PathVariable String caseId) {

        log.info("Estudiante {} cancelando caso: {}", studentId, caseId);
        return exceptionalCaseService.cancelCase(caseId, studentId);
    }

    /**
     * Proporcionar información adicional
     */
    @PutMapping("/students/{studentId}/cases/{caseId}/provide-info")
    public ExceptionalCaseResponseDTO provideAdditionalInfo(
            @PathVariable String studentId,
            @PathVariable String caseId,
            @RequestParam String information) {

        log.info("Estudiante {} proporcionando información para caso: {}", studentId, caseId);
        return exceptionalCaseService.provideAdditionalInfo(caseId, studentId, information);
    }

    // ========== ENDPOINTS PARA ADMINISTRADORES/DECANOS ==========

    /**
     * Obtener todos los casos
     */
    @GetMapping("/admin")
    public List<ExceptionalCaseResponseDTO> getAllCases(@RequestParam String employeeCode) {
        log.info("Administrador/Decano {} obteniendo todos los casos", employeeCode);
        return exceptionalCaseService.getAllCases(employeeCode);
    }

    /**
     * Obtener casos por estado
     */
    @GetMapping("/admin/status/{status}")
    public List<ExceptionalCaseResponseDTO> getCasesByStatus(
            @RequestParam String employeeCode,
            @PathVariable String status) {

        log.info("Administrador/Decano {} obteniendo casos por estado: {}", employeeCode, status);
        return exceptionalCaseService.getCasesByStatus(employeeCode, status);
    }

    /**
     * Obtener casos pendientes
     */
    @GetMapping("/admin/pending")
    public List<ExceptionalCaseResponseDTO> getPendingCases(@RequestParam String employeeCode) {
        log.info("Administrador/Decano {} obteniendo casos pendientes", employeeCode);
        return exceptionalCaseService.getPendingCases(employeeCode);
    }

    /**
     * Obtener casos urgentes
     */
    @GetMapping("/admin/urgent")
    public List<ExceptionalCaseResponseDTO> getUrgentCases(@RequestParam String employeeCode) {
        log.info("Administrador/Decano {} obteniendo casos urgentes", employeeCode);
        return exceptionalCaseService.getUrgentCases(employeeCode);
    }

    /**
     * Obtener casos asignados
     */
    @GetMapping("/admin/assigned")
    public List<ExceptionalCaseResponseDTO> getAssignedCases(@RequestParam String employeeCode) {
        log.info("Administrador/Decano {} obteniendo casos asignados", employeeCode);
        return exceptionalCaseService.getAssignedCases(employeeCode);
    }

    /**
     * Obtener un caso específico
     */
    @GetMapping("/admin/{caseId}")
    public ExceptionalCaseResponseDTO getCaseById(
            @PathVariable String caseId,
            @RequestParam String employeeCode) {

        log.info("Administrador/Decano {} obteniendo caso: {}", employeeCode, caseId);
        return exceptionalCaseService.getCaseById(caseId, employeeCode);
    }

    /**
     * Asignar un caso
     */
    @PutMapping("/admin/{caseId}/assign")
    public ExceptionalCaseResponseDTO assignCase(
            @PathVariable String caseId,
            @RequestParam String assignedBy,
            @RequestParam String assignTo) {

        log.info("{} asignando caso {} a {}", assignedBy, caseId, assignTo);
        return exceptionalCaseService.assignCase(caseId, assignedBy, assignTo);
    }

    /**
     * Solicitar más información
     */
    @PutMapping("/admin/{caseId}/request-info")
    public ExceptionalCaseResponseDTO requestMoreInfo(
            @PathVariable String caseId,
            @RequestParam String employeeCode,
            @RequestParam String informationNeeded,
            @RequestParam(required = false, defaultValue = "5") Integer daysToRespond) {

        log.info("{} solicitando información para caso {}: {}", employeeCode, caseId, informationNeeded);
        return exceptionalCaseService.requestMoreInfo(caseId, employeeCode, informationNeeded, daysToRespond);
    }

    /**
     * Resolver un caso
     */
    @PutMapping("/admin/{caseId}/resolve")
    public ExceptionalCaseResponseDTO resolveCase(
            @PathVariable String caseId,
            @Valid @RequestBody ExceptionalCaseResolutionDTO resolutionDTO) {

        log.info("{} resolviendo caso {} como {}", resolutionDTO.getEmployeeCode(), caseId, resolutionDTO.getResolution());
        return exceptionalCaseService.resolveCase(caseId, resolutionDTO);
    }

    /**
     * Obtener estadísticas de casos
     */
    @GetMapping("/admin/stats")
    public ExceptionalCaseStatsDTO getCaseStats(@RequestParam String employeeCode) {
        log.info("Administrador/Decano {} obteniendo estadísticas de casos", employeeCode);
        return exceptionalCaseService.getCaseStats(employeeCode);
    }

    // ========== ENDPOINTS PÚBLICOS/MIXTOS ==========

    /**
     * Buscar caso por número de caso
     */
    @GetMapping("/search/{caseNumber}")
    public ExceptionalCaseResponseDTO searchCaseByNumber(
            @PathVariable String caseNumber,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String userRole) {

        log.info("Buscando caso por número: {} - usuario: {} ({})", caseNumber, userId, userRole);

        throw new UnsupportedOperationException("Método no implementado aún");
    }

    /**
     * Obtener casos recientes
     */
    @GetMapping("/recent")
    public List<ExceptionalCaseResponseDTO> getRecentCases(@RequestParam String employeeCode) {
        log.info("Obteniendo casos recientes para: {}", employeeCode);
        return exceptionalCaseService.getAllCases(employeeCode).stream()
                .sorted(Comparator.comparing(ExceptionalCaseResponseDTO::getCreationDate).reversed())
                .limit(10) // Limitar a 10 casos más recientes
                .collect(java.util.stream.Collectors.toList());
    }
}