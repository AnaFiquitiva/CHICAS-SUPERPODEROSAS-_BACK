package src.main.java.eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GrupoRequestDTO {

    @NotBlank(message = "El código del grupo es obligatorio")
    private String codigo;

    @NotNull(message = "Debe indicar la materia")
    private String materiaId;

    @NotBlank(message = "El profesor asignado es obligatorio")
    private String profesorId;
}
