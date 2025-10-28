package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.RoleAssignmentRequest;
import eci.edu.dosw.proyecto.dto.UserResponse;
import eci.edu.dosw.proyecto.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador REST para la gestión de usuarios y roles.
 * Funcionalidad: Asignación de Roles a Usuarios
 * - Gestión de roles por Administradores
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios y roles")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Asignar rol a usuario")
    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(@Valid @RequestBody RoleAssignmentRequest request) {
        UserResponse user = userService.assignRole(request);
        return ResponseEntity.ok(user);
    }
}