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
 * Clase que representa una alerta del sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "alerts")
public class Alert {
    @Id
    private String id;

    private AlertType type; // GROUP_CAPACITY_90, SYSTEM_ALERT, etc.
    private String title;
    private String description;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @DBRef
    private Group group;

    private boolean resolved;
    private LocalDateTime resolvedAt;

    @DBRef
    private User resolvedBy;

    private LocalDateTime createdAt;
}