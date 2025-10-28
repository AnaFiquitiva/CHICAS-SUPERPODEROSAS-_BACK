package eci.edu.dosw.proyecto.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Pruebas unitarias para la clase Notification")
class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id("notif-123")
                .type("INFO")
                .title("Bienvenido")
                .message("Mensaje de bienvenida")
                .read(false)
                .build();
    }

    @Test
    @DisplayName("markAsRead - Happy Path: debería marcar la notificación como leída y establecer la fecha de lectura")
    void markAsRead_happyPath_shouldMarkAsReadAndSetReadAt() {
        // Act
        notification.markAsRead();
        // Assert
        assertThat(notification.isRead()).isTrue();
        assertThat(notification.getReadAt()).isNotNull();
        assertThat(notification.getReadAt()).isAfter(LocalDateTime.now().minusSeconds(1));
    }
}