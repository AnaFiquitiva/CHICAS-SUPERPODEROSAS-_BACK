package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ExceptionalCaseResolutionDTO {
    @NotNull(message = "El código de empleado es requerido")
    private String employeeCode;

    @NotNull(message = "La resolución es requerida")
    private String resolution; // APPROVED o REJECTED

    private String comments;
    private String additionalInstructions;
}