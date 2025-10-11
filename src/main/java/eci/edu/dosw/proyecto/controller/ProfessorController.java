package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.ProfessorDTO;
import eci.edu.dosw.proyecto.dto.ProfessorPartialUpdateDTO;
import eci.edu.dosw.proyecto.service.interfaces.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Controlador REST para la gestión de profesores.
 * Permite crear, consultar, actualizar y eliminar profesores
 * según los permisos de los roles: ADMIN, DEAN y PROFESSOR.
 */
@RestController
@RequestMapping("/api/professors")
@Validated
public class ProfessorController {

    @Autowired
    private ProfessorService service;

    /**
     * Crea un nuevo profesor.
     * Solo accesible para usuarios con rol ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfessorDTO> create(@Valid @RequestBody ProfessorDTO dto) {
        ProfessorDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Lista todos los profesores o filtra por facultad o materia.
     * Accesible para roles ADMIN y DEAN.
     *
     * @param facultyId ID de la facultad (opcional).
     * @param subjectId ID de la materia (opcional).
     * @return Lista de profesores que cumplen los filtros.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DEAN')")
    public ResponseEntity<List<ProfessorDTO>> list(@RequestParam(required = false) String facultyId,
                                                   @RequestParam(required = false) String subjectId) {
        return ResponseEntity.ok(service.findAll(facultyId, subjectId));
    }

    /**
     * Obtiene los datos de un profesor por ID.
     * Accesible para ADMIN, DEAN y el mismo PROFESSOR..
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DEAN','PROFESSOR')")
    public ResponseEntity<ProfessorDTO> getById(@PathVariable String id, Principal principal) {
        String authenticatedId = principal != null ? principal.getName() : null;
        ProfessorDTO dto = service.findByIdWithPermissions(id, authenticatedId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualiza los datos de un profesor como ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfessorDTO> updateAsAdmin(@PathVariable String id, @Valid @RequestBody ProfessorDTO dto) {
        return ResponseEntity.ok(service.updateAsAdmin(id, dto));
    }

    /**
     * Permite que un profesor actualice su propia información parcial.
     */
    @PatchMapping("/{id}/self")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ProfessorDTO> updateSelf(@PathVariable String id, @Valid @RequestBody ProfessorPartialUpdateDTO dto, Principal principal) {
        String authenticatedProfessorId = principal.getName();
        return ResponseEntity.ok(service.updateSelf(id, dto, authenticatedProfessorId));
    }

    /**
     * Elimina un profesor (marcado como inactivo).
     * Solo accesible para ADMIN.

     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


