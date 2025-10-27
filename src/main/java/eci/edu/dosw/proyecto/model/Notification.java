package eci.edu.dosw.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
/**
 * Notificaciones del sistema para usuarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    private String type; // REQUEST_UPDATE, ALERT, INFO, WARNING, SUCCESS
    private String title;
    private String message;
    private boolean read;
    private String actionUrl; // URL para redirección si aplica

    @DBRef
    private User recipient;

    @DBRef
    private Request relatedRequest; // Opcional - para notificaciones de solicitud

    @DBRef
    private Alert relatedAlert; // Opcional - para notificaciones de alerta

    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    // Método para marcar como leída
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
}