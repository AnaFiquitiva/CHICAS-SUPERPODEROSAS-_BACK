package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.ManualAssignmentRequest;
import eci.edu.dosw.proyecto.dto.ManualAssignmentResponse;
import eci.edu.dosw.proyecto.model.AssignmentStatus;
import eci.edu.dosw.proyecto.service.interfaces.ManualAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de asignaciones manuales de estudiantes.
 *
 * Funcionalidades: Asignaciones Manuales
 * - Asignar/retirar estudiantes de grupos o materias
 * - Validación de restricciones y sobrepaso de límites
 */
@RestController
@RequestMapping("/api/manual-assignments")
@RequiredArgsConstructor
@Tag(name = "Asignaciones Manuales", description = "Gestión de asignaciones manuales de estudiantes")
public class ManualAssignmentController {

    private final ManualAssignmentService manualAssignmentService;

    @Operation(summary = "Crear asignación manual")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> createManualAssignment(@Valid @RequestBody ManualAssignmentRequest assignmentRequest) {
        ManualAssignmentResponse assignment = manualAssignmentService.createManualAssignment(assignmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @Operation(summary = "Obtener asignación manual por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> getManualAssignmentById(@PathVariable String id) {
        ManualAssignmentResponse assignment = manualAssignmentService.getManualAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Listar asignaciones por estudiante")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ManualAssignmentResponse>> getManualAssignmentsByStudent(@PathVariable String studentId) {
        List<ManualAssignmentResponse> assignments = manualAssignmentService.getManualAssignmentsByStudent(studentId);
        return ResponseEntity.ok(assignments);
    }

    @Operation(summary = "Listar asignaciones por facultad")
    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ManualAssignmentResponse>> getManualAssignmentsByFaculty(@PathVariable String facultyId) {
        List<ManualAssignmentResponse> assignments = manualAssignmentService.getManualAssignmentsByFaculty(facultyId);
        return ResponseEntity.ok(assignments);
    }

    @Operation(summary = "Ejecutar asignación manual")
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> executeManualAssignment(@PathVariable String id) {
        ManualAssignmentResponse assignment = manualAssignmentService.executeManualAssignment(id);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Cancelar asignación manual")
    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Void> cancelManualAssignment(@PathVariable String id) {
        manualAssignmentService.cancelManualAssignment(id);
        return ResponseEntity.noContent().build();
    }

    // === ASIGNACIONES ESPECÍFICAS POR TIPO ===

    @Operation(summary = "Asignar estudiante a materia")
    @PostMapping("/assign-subject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> assignStudentToSubject(@Valid @RequestBody ManualAssignmentRequest assignmentRequest) {
        ManualAssignmentResponse assignment = manualAssignmentService.assignStudentToSubject(assignmentRequest);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Asignar estudiante a grupo")
    @PostMapping("/assign-group")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> assignStudentToGroup(@Valid @RequestBody ManualAssignmentRequest assignmentRequest) {
        ManualAssignmentResponse assignment = manualAssignmentService.assignStudentToGroup(assignmentRequest);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Retirar estudiante de materia")
    @PostMapping("/withdraw-subject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> withdrawStudentFromSubject(@Valid @RequestBody ManualAssignmentRequest assignmentRequest) {
        ManualAssignmentResponse assignment = manualAssignmentService.withdrawStudentFromSubject(assignmentRequest);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Retirar estudiante de grupo")
    @PostMapping("/withdraw-group")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> withdrawStudentFromGroup(@Valid @RequestBody ManualAssignmentRequest assignmentRequest) {
        ManualAssignmentResponse assignment = manualAssignmentService.withdrawStudentFromGroup(assignmentRequest);
        return ResponseEntity.ok(assignment);
    }

    // === VALIDACIÓN Y PERMISOS ===

    @Operation(summary = "Validar asignación manual")
    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ManualAssignmentResponse> validateManualAssignment(@PathVariable String id) {
        ManualAssignmentResponse assignment = manualAssignmentService.validateManualAssignment(id);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Verificar permiso para sobrepasar prerrequisitos")
    @GetMapping("/can-override-prerequisites/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Boolean> canOverridePrerequisites(@PathVariable String studentId, @PathVariable String subjectId) {
        boolean canOverride = manualAssignmentService.canOverridePrerequisites(studentId, subjectId);
        return ResponseEntity.ok(canOverride);
    }

    @Operation(summary = "Verificar permiso para sobrepasar cupo")
    @GetMapping("/can-override-capacity/group/{groupId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Boolean> canOverrideCapacity(@PathVariable String groupId) {
        boolean canOverride = manualAssignmentService.canOverrideCapacity(groupId);
        return ResponseEntity.ok(canOverride);
    }

    @Operation(summary = "Verificar permiso para sobrepasar límite de créditos")
    @GetMapping("/can-override-credit-limit/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Boolean> canOverrideCreditLimit(@PathVariable String studentId) {
        boolean canOverride = manualAssignmentService.canOverrideCreditLimit(studentId);
        return ResponseEntity.ok(canOverride);
    }

    // === OPERACIONES BULK ===

    @Operation(summary = "Listar asignaciones pendientes")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ManualAssignmentResponse>> getPendingManualAssignments() {
        List<ManualAssignmentResponse> assignments = manualAssignmentService.getPendingManualAssignments();
        return ResponseEntity.ok(assignments);
    }

    @Operation(summary = "Listar asignaciones por estado")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ManualAssignmentResponse>> getManualAssignmentsByStatus(@PathVariable String status) {
        AssignmentStatus assignmentStatus = AssignmentStatus.valueOf(status.toUpperCase());
        List<ManualAssignmentResponse> assignments = manualAssignmentService.getManualAssignmentsByStatus(assignmentStatus);
        return ResponseEntity.ok(assignments);
    }
}