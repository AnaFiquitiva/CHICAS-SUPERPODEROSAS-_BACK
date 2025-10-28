package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProgramRequest {
    @NotBlank(message = "Código es requerido")
    private String code;

    @NotBlank(message = "Nombre es requerido")
    private String name;

    @NotBlank(message = "Facultad es requerida")
    private String facultyId;

    private String description;
    private Integer totalCredits;
    private Integer durationSemesters;
}
