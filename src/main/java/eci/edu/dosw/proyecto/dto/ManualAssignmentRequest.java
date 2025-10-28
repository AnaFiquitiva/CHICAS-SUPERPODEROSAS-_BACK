package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.AssignmentType;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ManualAssignmentRequest {
    @NotBlank(message = "Estudiante es requerido")
    private String studentId;

    @NotNull(message = "Tipo de asignación es requerido")
    private AssignmentType type;

    // Dependiendo del tipo
    private String subjectId;
    private String groupId;

    @NotBlank(message = "Justificación es requerida")
    private String justification;

    private String reason;
    private Boolean overridePrerequisites;
    private Boolean overrideCapacity;
    private Boolean overrideCreditLimit;
    private String overrideJustification;
}

