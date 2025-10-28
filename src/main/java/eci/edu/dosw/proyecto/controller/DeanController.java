package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.DeanRequest;
import eci.edu.dosw.proyecto.dto.DeanResponse;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
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
 * Controlador REST para la gestión de decanos.
 * Funcionalidades: Registro de Decanos (CRUD)
 * - Gestión de decanos por el Administrador
 * - Consulta de información por los decanos
 */
@RestController
@RequestMapping("/api/deans")
@RequiredArgsConstructor
@Tag(name = "Decanos", description = "Gestión CRUD de decanos")
public class DeanController {

    private final DeanService deanService;

    @Operation(summary = "Crear decano")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeanResponse> createDean(@Valid @RequestBody DeanRequest deanRequest) {
        DeanResponse dean = deanService.createDean(deanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(dean);
    }

    @Operation(summary = "Obtener decano por ID")
    @GetMapping("/{id}")
    public ResponseEntity<DeanResponse> getDeanById(@PathVariable String id) {
        DeanResponse dean = deanService.getDeanById(id);
        return ResponseEntity.ok(dean);
    }

    @Operation(summary = "Obtener decano por correo")
    @GetMapping("/email/{email}")
    public ResponseEntity<DeanResponse> getDeanByEmail(@PathVariable String email) {
        DeanResponse dean = deanService.getDeanByEmail(email);
        return ResponseEntity.ok(dean);
    }

    @Operation(summary = "Listar decanos por facultad")
    @GetMapping("/faculty/{facultsyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeanResponse>> getDeansByFaculty(@PathVariable String facultyId) {
        List<DeanResponse> deans = deanService.getDeansByFaculty(facultyId);
        return ResponseEntity.ok(deans);
    }

    @Operation(summary = "Listar todos los decanos activos")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DeanResponse>> getAllActiveDeans() {
        List<DeanResponse> deans = deanService.getAllActiveDeans();
        return ResponseEntity.ok(deans);
    }

    @Operation(summary = "Actualizar decano")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeanResponse> updateDean(@PathVariable String id, @Valid @RequestBody DeanRequest deanRequest) {
        DeanResponse dean = deanService.updateDean(id, deanRequest);
        return ResponseEntity.ok(dean);
    }

    @Operation(summary = "Desactivar decano")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateDean(@PathVariable String id) {
        deanService.deactivateDean(id);
        return ResponseEntity.noContent().build();
    }
}