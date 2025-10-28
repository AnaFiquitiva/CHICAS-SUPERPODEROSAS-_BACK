package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.service.interfaces.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de solicitudes académicas.
 * Funcionalidades: Responder Solicitudes y Gestión de Casos Especiales
 * - Aprobación, rechazo y solicitud de información adicional
 * - Aprobación especial para casos excepcionales
 * - Consulta de historial y estadísticas
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Solicitudes", description = "Gestión de solicitudes académicas")
public class RequestController {

    private final RequestService requestService;
    private final StudentRepository studentRepository;

    @Operation(summary = "Crear solicitud")
    @PostMapping
    public ResponseEntity<RequestResponse> createRequest(@Valid @RequestBody RequestCreateRequest requestCreateRequest) {
        RequestResponse request = requestService.createRequest(requestCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @Operation(summary = "Obtener solicitud por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RequestResponse> getRequestById(@PathVariable String id) {
        RequestResponse request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Obtener solicitud por número")
    @GetMapping("/number/{requestNumber}")
    public ResponseEntity<RequestResponse> getRequestByNumber(@PathVariable String requestNumber) {
        RequestResponse request = requestService.getRequestByNumber(requestNumber);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Listar solicitudes por estudiante")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RequestResponse>> getRequestsByStudent(@PathVariable String studentId) {
        List<RequestResponse> requests = requestService.getRequestsByStudent(studentId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Listar solicitudes por estado")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequestResponse>> getRequestsByStatus(@PathVariable String status) {
        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());
        List<RequestResponse> requests = requestService.getRequestsByStatus(requestStatus);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Listar todas las solicitudes")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequestResponse>> getAllRequests() {
        List<RequestResponse> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Actualizar solicitud")
    @PatchMapping("/{id}")
    public ResponseEntity<RequestResponse> updateRequest(@PathVariable String id, @Valid @RequestBody RequestUpdateRequest requestUpdateRequest) {
        RequestResponse request = requestService.updateRequest(id, requestUpdateRequest);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Cancelar solicitud")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable String id) {
        // Obtener el ID del estudiante autenticado del contexto de seguridad
        String studentId = getCurrentAuthenticatedStudentId();
        requestService.cancelRequest(id, studentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar solicitudes por facultad")
    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<RequestResponse>> getRequestsByFaculty(@PathVariable String facultyId) {
        List<RequestResponse> requests = requestService.getRequestsByFaculty(facultyId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Listar solicitudes por facultad y estado")
    @GetMapping("/faculty/{facultyId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<RequestResponse>> getRequestsByFacultyAndStatus(@PathVariable String facultyId, @PathVariable String status) {
        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());
        List<RequestResponse> requests = requestService.getRequestsByFacultyAndStatus(facultyId, requestStatus);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Listar solicitudes por prioridad (facultad)")
    @GetMapping("/faculty/{facultyId}/priority")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<RequestBasicResponse>> getFacultyRequestsByPriority(@PathVariable String facultyId) {
        List<RequestBasicResponse> requests = requestService.getFacultyRequestsByPriority(facultyId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Listar solicitudes por orden de llegada (facultad)")
    @GetMapping("/faculty/{facultyId}/arrival")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<RequestBasicResponse>> getFacultyRequestsByArrival(@PathVariable String facultyId) {
        List<RequestBasicResponse> requests = requestService.getFacultyRequestsByArrival(facultyId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Aprobar solicitud")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<RequestResponse> approveRequest(@PathVariable String id, @Valid @RequestBody RequestDecisionRequest decisionRequest) {
        String processedBy = getCurrentAuthenticatedUserId();
        RequestResponse request = requestService.approveRequest(id, decisionRequest, processedBy);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Rechazar solicitud")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<RequestResponse> rejectRequest(@PathVariable String id, @Valid @RequestBody RequestDecisionRequest decisionRequest) {
        String processedBy = getCurrentAuthenticatedUserId();
        RequestResponse request = requestService.rejectRequest(id, decisionRequest, processedBy);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Solicitar información adicional")
    @PostMapping("/{id}/additional-info")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<RequestResponse> requestAdditionalInfo(@PathVariable String id, @Valid @RequestBody RequestDecisionRequest decisionRequest) {
        String processedBy = getCurrentAuthenticatedUserId();
        RequestResponse request = requestService.requestAdditionalInfo(id, decisionRequest, processedBy);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Aprobación especial")
    @PostMapping("/{id}/special-approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RequestResponse> processSpecialApproval(@PathVariable String id, @Valid @RequestBody RequestDecisionRequest decisionRequest) {
        String processedBy = getCurrentAuthenticatedUserId();
        RequestResponse request = requestService.processSpecialApproval(id, decisionRequest, processedBy);
        return ResponseEntity.ok(request);
    }

    @Operation(summary = "Listar solicitudes pendientes por facultad")
    @GetMapping("/faculty/{facultyId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<RequestResponse>> getPendingRequestsByFaculty(@PathVariable String facultyId) {
        List<RequestResponse> requests = requestService.getPendingRequestsByFaculty(facultyId);
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Listar solicitudes globales por prioridad")
    @GetMapping("/global/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequestBasicResponse>> getGlobalRequestsByPriority() {
        List<RequestBasicResponse> requests = requestService.getGlobalRequestsByPriority();
        return ResponseEntity.ok(requests);
    }

    @Operation(summary = "Obtener historial de solicitud")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<RequestHistoryResponse>> getRequestHistory(@PathVariable String id) {
        List<RequestHistoryResponse> history = requestService.getRequestHistory(id);
        return ResponseEntity.ok(history);
    }

    @Operation(summary = "Obtener estadísticas de solicitudes")
    @GetMapping("/stats/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<RequestStatsResponse> getRequestStatistics(@PathVariable String facultyId) {
        RequestStatsResponse stats = requestService.getRequestStatistics(facultyId);
        return ResponseEntity.ok(stats);
    }



    private String getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Usuario no autenticado");
        }
        return authentication.getName();
    }

    private String getCurrentAuthenticatedStudentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        // Obtener el username (que es el email institucional del estudiante)
        String username = authentication.getName();

        // Buscar el estudiante por su correo institucional
        Student student = studentRepository.findByInstitutionalEmail(username)
                .orElseThrow(() -> new NotFoundException("Estudiante", "usuario autenticado"));

        return student.getId();
    }
}