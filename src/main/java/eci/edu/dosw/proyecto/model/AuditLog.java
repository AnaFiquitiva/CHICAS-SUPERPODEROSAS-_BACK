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
 * Clase que representa un registro de auditoría del sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;

    private String action; // CREATE, UPDATE, DELETE, APPROVE, etc.
    private String entityType; // STUDENT, REQUEST, GROUP, etc.
    private String entityId;
    private String description;
    private String oldValues;
    private String newValues;

    @DBRef
    private User performedBy;

    private LocalDateTime performedAt;
    private String ipAddress;
}
