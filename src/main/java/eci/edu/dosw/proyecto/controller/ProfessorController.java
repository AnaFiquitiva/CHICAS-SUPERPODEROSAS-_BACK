package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.GroupResponse;
import eci.edu.dosw.proyecto.dto.ProfessorRequest;
import eci.edu.dosw.proyecto.dto.ProfessorResponse;
import eci.edu.dosw.proyecto.service.interfaces.ProfessorService;
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
 * Controlador REST para la gestión de profesores.
 *
 * Funcionalidades: Registro de Profesores (CRUD)
 * - Gestión de profesores por Administrador y Decano
 * - Consulta y edición limitada por profesores
 */
@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
@Tag(name = "Profesores", description = "Gestión CRUD de profesores")
public class ProfessorController {

    private final ProfessorService professorService;

    @Operation(summary = "Crear profesor")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ProfessorResponse> createProfessor(@Valid @RequestBody ProfessorRequest professorRequest) {
        ProfessorResponse professor = professorService.createProfessor(professorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(professor);
    }

    @Operation(summary = "Obtener profesor por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponse> getProfessorById(@PathVariable String id) {
        ProfessorResponse professor = professorService.getProfessorById(id);
        return ResponseEntity.ok(professor);
    }

    @Operation(summary = "Obtener profesor por código")
    @GetMapping("/code/{code}")
    public ResponseEntity<ProfessorResponse> getProfessorByCode(@PathVariable String code) {
        ProfessorResponse professor = professorService.getProfessorByCode(code);
        return ResponseEntity.ok(professor);
    }

    @Operation(summary = "Obtener profesor por correo")
    @GetMapping("/email/{email}")
    public ResponseEntity<ProfessorResponse> getProfessorByEmail(@PathVariable String email) {
        ProfessorResponse professor = professorService.getProfessorByEmail(email);
        return ResponseEntity.ok(professor);
    }

    @Operation(summary = "Listar profesores por facultad")
    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ProfessorResponse>> getProfessorsByFaculty(@PathVariable String facultyId) {
        List<ProfessorResponse> professors = professorService.getProfessorsByFaculty(facultyId);
        return ResponseEntity.ok(professors);
    }

    @Operation(summary = "Listar todos los profesores activos")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<List<ProfessorResponse>> getAllActiveProfessors() {
        List<ProfessorResponse> professors = professorService.getAllActiveProfessors();
        return ResponseEntity.ok(professors);
    }

    @Operation(summary = "Listar grupos del profesor")
    @GetMapping("/{professorId}/groups")
    public ResponseEntity<List<GroupResponse>> getProfessorGroups(@PathVariable String professorId) {
        List<GroupResponse> groups = professorService.getProfessorGroups(professorId);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Actualizar profesor")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<ProfessorResponse> updateProfessor(@PathVariable String id, @Valid @RequestBody ProfessorRequest professorRequest) {
        ProfessorResponse professor = professorService.updateProfessor(id, professorRequest);
        return ResponseEntity.ok(professor);
    }

    @Operation(summary = "Desactivar profesor")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<Void> deactivateProfessor(@PathVariable String id) {
        professorService.deactivateProfessor(id);
        return ResponseEntity.noContent().build();
    }
}