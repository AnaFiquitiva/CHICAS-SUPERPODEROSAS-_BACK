package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.AuthResponse;
import eci.edu.dosw.proyecto.dto.ChangePasswordRequest;
import eci.edu.dosw.proyecto.dto.LoginRequest;
import eci.edu.dosw.proyecto.dto.UserResponse;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación y gestión de usuarios.
 *
 * Funcionalidades: Autenticación de Usuarios
 * - Inicio de sesión y cierre de sesión
 * - Cambio de contraseña
 * - Gestión de sesiones inactivas
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Gestión de autenticación de usuarios")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserResponse user = userService.authenticate(loginRequest);
        // En implementación real, aquí iría la generación de JWT
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(user);
        authResponse.setRole(user.getRole());
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Cambiar contraseña")
    @PostMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String userId = getCurrentAuthenticatedUserId();
        UserResponse user = userService.changePassword(request, userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obtener usuario actual")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        String userId = getCurrentAuthenticatedUserId();
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }


    private String getCurrentAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario", "autenticado"));

        return user.getId();
    }
}