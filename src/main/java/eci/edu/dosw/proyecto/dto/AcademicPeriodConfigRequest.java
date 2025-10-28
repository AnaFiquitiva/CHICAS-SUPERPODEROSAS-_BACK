package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AcademicPeriodConfigRequest {
    @NotBlank(message = "Nombre del período es requerido")
    private String periodName;

    private String description;

    @NotNull(message = "Fecha de inicio es requerida")
    private LocalDateTime startDate;

    @NotNull(message = "Fecha de fin es requerida")
    private LocalDateTime endDate;

    private String facultyId;
    private String allowedRequestTypes;
}
