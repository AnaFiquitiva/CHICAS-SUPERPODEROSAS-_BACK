package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InscripcionDTO {
    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String estudianteId;
    @NotBlank(message = "El ID del grupo es obligatorio")
    private String grupoId;
}
