package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.NotificationRequest;
import eci.edu.dosw.proyecto.dto.NotificationResponse;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.User;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.NotificationService;
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
 * Controlador REST para la gestión de notificaciones del sistema.
 *
 * Funcionalidades: Notificaciones
 * - Creación, consulta y marcado como leídas
 * - Notificaciones automáticas para solicitudes y alertas
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones del sistema")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Operation(summary = "Crear notificación")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEAN')")
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        NotificationResponse notification = notificationService.createNotification(notificationRequest);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Obtener notificaciones del usuario")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUserNotifications() {
        String userId = getCurrentAuthenticatedUserId();
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Obtener notificaciones no leídas")
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadUserNotifications() {
        String userId = getCurrentAuthenticatedUserId();
        List<NotificationResponse> notifications = notificationService.getUnreadUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Marcar notificación como leída")
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable String id) {
        NotificationResponse notification = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Marcar todas las notificaciones como leídas")
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        String userId = getCurrentAuthenticatedUserId();
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener conteo de notificaciones no leídas")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        String userId = getCurrentAuthenticatedUserId();
        long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }

    // === MÉTODOS AUXILIARES DE SEGURIDAD ===

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