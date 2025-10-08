package eci.edu.dosw.proyecto.dto;


import eci.edu.dosw.proyecto.model.RequestType;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChangeRequestRequestDTO {
    @NotNull(message = "El tipo de solicitud es requerido")
    private RequestType type;

    private String currentSubjectId;
    private String currentGroupId;
    private String targetSubjectId;
    private String targetGroupId;

    private List<PlanChangeDetailRequestDTO> planChanges;
    private String observations;


}