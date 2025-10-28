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
 * Clase que representa una solicitud de cambio académico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "requests")
public class Request {
    @Id
    private String id;

    private String requestNumber; // Número de radicado único
    private RequestType type; // CHANGE_GROUP, CHANGE_SUBJECT, STUDY_PLAN_CHANGE
    private RequestStatus status; // PENDING, APPROVED, REJECTED, ADDITIONAL_INFO, CANCELLED
    private String description;
    private String observations;
    private String justification;
    private Integer priorityScore;

    @DBRef
    private Student student;

    @DBRef
    private Subject currentSubject;

    @DBRef
    private Group currentGroup;
    @DBRef
    private Subject requestedSubject;

    @DBRef
    private Group requestedGroup;

    @DBRef
    private User processedBy; // Quien aprobó/rechazó

    private LocalDateTime processedAt;
    private boolean specialApproval;
    private String specialApprovalJustification;

    @DBRef
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Método para calcular prioridad (más antiguo = mayor prioridad)
    public void calculatePriority() {
        if (createdAt != null) {
            // Prioridad basada en antigüedad (más viejo = número menor = mayor prioridad)
            long daysOld = java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
            this.priorityScore = (int) daysOld;
        }
    }
}
