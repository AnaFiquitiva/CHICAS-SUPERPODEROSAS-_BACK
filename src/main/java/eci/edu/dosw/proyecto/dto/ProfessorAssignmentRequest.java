package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para solicitud de asignación o retiro de profesor a un grupo.
 * Contiene los datos necesarios para realizar la operación.
 */
@Data
public class ProfessorAssignmentRequest {

    @NotNull(message = "El ID del grupo es obligatorio")
    private String groupId;

    @NotNull(message = "El ID del profesor es obligatorio")
    private String professorId;

    /** Indica si se puede reemplazar a un profesor ya asignado */
    private Boolean forceReplace = false;

    /** ID del usuario (decano/administrador) que realiza la asignación */
    private String assignedBy;
}