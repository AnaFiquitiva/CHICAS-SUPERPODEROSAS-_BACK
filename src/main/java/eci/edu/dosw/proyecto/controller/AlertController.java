// eci.edu.dosw.proyecto.controller.AlertController.java
package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.AlertResponse;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Alert;
import eci.edu.dosw.proyecto.model.AlertType;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador REST para la gestión de alertas del sistema.
 * Funcionalidades: Alertas del Sistema
 * - Consulta de alertas activas y por tipo
 * - Resolución de alertas por administradores
 */

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "Gestión de alertas del sistema")
public class AlertController {

    private final AlertService alertService;
    private final UserRepository userRepository;

    @Operation(summary = "Obtener alertas activas")
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        List<AlertResponse> alerts = alertService.getActiveAlerts().stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @Operation(summary = "Obtener alertas por tipo")
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getAlertsByType(@PathVariable String type) {
        AlertType alertType = AlertType.valueOf(type.toUpperCase());
        List<AlertResponse> alerts = alertService.getAlertsByType(alertType).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(alerts);
    }

    @Operation(summary = "Resolver alerta")
    @PatchMapping("/{alertId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertResponse> resolveAlert(@PathVariable String alertId) {
        String resolvedBy = getCurrentAuthenticatedUserId();
        AlertResponse alert = mapToResponse(alertService.resolveAlert(alertId, resolvedBy));
        return ResponseEntity.ok(alert);
    }


    private AlertResponse mapToResponse(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.setId(alert.getId());
        response.setType(alert.getType().name());
        response.setTitle(alert.getTitle());
        response.setDescription(alert.getDescription());
        response.setSeverity(alert.getSeverity());
        response.setResolved(alert.isResolved());
        response.setResolvedAt(alert.getResolvedAt());
        response.setCreatedAt(alert.getCreatedAt());
        return response;
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