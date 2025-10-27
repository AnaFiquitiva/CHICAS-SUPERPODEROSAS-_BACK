package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCreateRequest {
    @NotNull(message = "Tipo de solicitud es requerido")
    private RequestType type;
    private Long studentId;


    private String description;

    // Para cambio de grupo/materia
    private String currentGroupId;
    private String requestedGroupId;
    private String requestedSubjectId;



}
