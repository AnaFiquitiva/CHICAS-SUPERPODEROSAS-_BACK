package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class PlanChangeDetailRequestDTO {
    @NotNull(message = "La acción es requerida")
    private String action;

    private String subjectId;
    private String groupId;
    private String replacementSubjectId;
    private String replacementGroupId;

    @NotNull(message = "La razón es requerida")
    private String reason;
}