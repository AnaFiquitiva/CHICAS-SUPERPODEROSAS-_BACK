package src.main.java.eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MateriaRequestDTO {

    @NotBlank(message = "El nombre de la materia es obligatorio")
    private String nombre;

    @NotBlank(message = "El código de la materia es obligatorio")
    private String codigo;

    @NotBlank(message = "La descripción de la materia es obligatoria")
    private String descripcion;

    @NotBlank(message = "El ID del periodo académico es obligatorio")
    private String periodoAcademicoId;
}
