package eci.edu.dosw.proyecto.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "change_requests")
/*
 * SOLICITUD DE CAMBIO
 */
public class ChangeRequest {
    @Id
    private String id;
    private String requestNumber;
    private String studentId;
    private LocalDateTime creationDate;
    private RequestType type;
    private RequestStatus status;
    private Integer priority;

    private String currentSubjectId;    // Solo para GROUP_CHANGE y SUBJECT_CHANGE
    private String currentGroupId;      // Solo para GROUP_CHANGE y SUBJECT_CHANGE
    private String targetSubjectId;     // Para todos los tipos
    private String targetGroupId;       // Para todos los tipos

    private List<PlanChangeDetail> planChanges; // Lista de cambios en el plan

    private String observations;
    private List<RequestHistory> history;

    public String getReason() {
        return observations;
    }
    private String assignedTo; // Administrador/Decano asignado
    private LocalDateTime reviewDate; // Fecha de revisión
    private String reviewComments; // Comentarios del revisor
    private String rejectionReason; // Razón específica del rechazo
    private String requiredInformation; // Información requerida
    private LocalDateTime responseDeadline; // Fecha límite para respuesta
}