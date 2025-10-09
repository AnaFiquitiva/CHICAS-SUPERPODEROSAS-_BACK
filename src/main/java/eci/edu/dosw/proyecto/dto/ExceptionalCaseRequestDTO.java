package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ExceptionalCaseRequestDTO {
    @NotNull(message = "El título es requerido")
    private String title;

    @NotNull(message = "La descripción es requerida")
    private String description;

    @NotNull(message = "El tipo de caso es requerido")
    private String caseType;

    @NotNull(message = "La justificación es requerida")
    private String justification;

    private String supportingDocuments;
    private Integer priority = 2; // Prioridad media por defecto
}