package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupRequest {
    @NotBlank(message = "Código de grupo es requerido")
    private String groupCode;

    @NotBlank(message = "Materia es requerida")
    private String subjectId;

    @NotNull(message = "Capacidad máxima es requerida")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer maxCapacity;

    private String professorId;
}
