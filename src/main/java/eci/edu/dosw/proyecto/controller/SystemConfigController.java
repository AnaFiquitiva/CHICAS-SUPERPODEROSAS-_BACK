package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.AlertType;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la configuración global del sistema.
 *
 * Funcionalidades: Configuración del Sistema
 * - Parámetros ajustables (cupo máximo, alertas, sesiones)
 * - Configuración de períodos académicos
 * - Gestión de alertas del sistema
 */
@RestController
@RequestMapping("/api/system-config")
@RequiredArgsConstructor
@Tag(name = "Configuración del Sistema", description = "Gestión de configuración global")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final UserRepository userRepository;
    @Operation(summary = "Obtener configuración del sistema")
    @GetMapping
    public ResponseEntity<SystemConfigResponse> getSystemConfig() {
        SystemConfigResponse config = systemConfigService.getSystemConfig();
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Actualizar configuración del sistema")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemConfigResponse> updateSystemConfig(@Valid @RequestBody SystemConfigRequest systemConfigRequest) {
        SystemConfigResponse config = systemConfigService.updateSystemConfig(systemConfigRequest);
        return ResponseEntity.ok(config);
    }

    // === CONFIGURACIÓN DE PERÍODOS ACADÉMICOS ===

    @Operation(summary = "Crear configuración de período académico")
    @PostMapping("/periods")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriodConfigResponse> createAcademicPeriodConfig(@Valid @RequestBody AcademicPeriodConfigRequest configRequest) {
        AcademicPeriodConfigResponse config = systemConfigService.createAcademicPeriodConfig(configRequest);
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Actualizar configuración de período académico")
    @PutMapping("/periods/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicPeriodConfigResponse> updateAcademicPeriodConfig(@PathVariable String id, @Valid @RequestBody AcademicPeriodConfigRequest configRequest) {
        AcademicPeriodConfigResponse config = systemConfigService.updateAcademicPeriodConfig(id, configRequest);
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Listar configuraciones de períodos activos")
    @GetMapping("/periods/active")
    public ResponseEntity<List<AcademicPeriodConfigResponse>> getActiveAcademicPeriodConfigs() {
        List<AcademicPeriodConfigResponse> configs = systemConfigService.getActiveAcademicPeriodConfigs();
        return ResponseEntity.ok(configs);
    }

    @Operation(summary = "Desactivar configuración de período académico")
    @DeleteMapping("/periods/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateAcademicPeriodConfig(@PathVariable String id) {
        systemConfigService.deactivateAcademicPeriodConfig(id);
        return ResponseEntity.noContent().build();
    }

    // === GESTIÓN DE ALERTAS ===

    @Operation(summary = "Obtener alertas activas")
    @GetMapping("/alerts/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        List<AlertResponse> alerts = systemConfigService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    @Operation(summary = "Obtener alertas por tipo")
    @GetMapping("/alerts/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getAlertsByType(@PathVariable String type) {
        AlertType alertType = AlertType.valueOf(type.toUpperCase());
        List<AlertResponse> alerts = systemConfigService.getAlertsByType(alertType);
        return ResponseEntity.ok(alerts);
    }

    @Operation(summary = "Resolver alerta")
    @PatchMapping("/alerts/{alertId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertResponse> resolveAlert(@PathVariable String alertId) {
        String resolvedBy = getCurrentAuthenticatedUserId();
        AlertResponse alert = systemConfigService.resolveAlert(alertId, resolvedBy);
        return ResponseEntity.ok(alert);
    }

    // === MONITOREO ===

    @Operation(summary = "Monitorear ocupación de grupos")
    @PostMapping("/monitor/occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> monitorGroupOccupancy() {
        systemConfigService.monitorGroupOccupancy();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener grupos cerca de capacidad")
    @GetMapping("/monitor/high-occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GroupResponse>> getGroupsNearingCapacity(@RequestParam(defaultValue = "0.9") Double threshold) {
        List<GroupResponse> groups = systemConfigService.getGroupsNearingCapacity(threshold);
        return ResponseEntity.ok(groups);
    }

    // === MÉTODOS AUXILIARES ===

    // En SystemConfigController.java
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