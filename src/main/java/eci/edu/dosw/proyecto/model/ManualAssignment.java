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
 * Asignaciones manuales por parte de Decanos o Administradores.
 * Funcionalidades: 39 (Asignar grupo), 40 (Retirar de grupo), 41 (Retirar materia), 42 (Asignar materia)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "manual_assignments")
public class ManualAssignment {
    @Id
    private String id;

    @DBRef
    private Student student;

    @DBRef
    private Subject subject;

    @DBRef
    private Group group;

    private AssignmentType type; // SUBJECT_ASSIGNMENT, GROUP_ASSIGNMENT, SUBJECT_WITHDRAWAL, GROUP_WITHDRAWAL
    private String reason;
    private String justification;

    // Campos de validación y restricciones
    private boolean overridePrerequisites;
    private boolean overrideCapacity;
    private boolean overrideCreditLimit;
    private String overrideJustification;

    // Resultado de validaciones automáticas
    private boolean prerequisitesValidated;
    private boolean capacityValidated;
    private boolean creditLimitValidated;
    private boolean scheduleConflictChecked;
    private String validationMessages;

    // Estado de la asignación
    private AssignmentStatus status; // PENDING, EXECUTED, FAILED, CANCELLED
    private String executionResult;

    @DBRef
    private User assignedBy; // Decano o Administrador

    private LocalDateTime assignedAt;
    private LocalDateTime executedAt;
    private LocalDateTime createdAt;

    // Método para validar asignación antes de ejecutar
    public boolean isValidForExecution() {
        return status == AssignmentStatus.PENDING &&
                student != null &&
                assignedBy != null &&
                ((type == AssignmentType.SUBJECT_ASSIGNMENT && subject != null) ||
                        (type == AssignmentType.GROUP_ASSIGNMENT && group != null && subject != null) ||
                        (type == AssignmentType.SUBJECT_WITHDRAWAL && subject != null) ||
                        (type == AssignmentType.GROUP_WITHDRAWAL && group != null));
    }
}

