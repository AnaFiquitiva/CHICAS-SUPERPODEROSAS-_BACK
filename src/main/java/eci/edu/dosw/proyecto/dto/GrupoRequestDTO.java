package src.main.java.eci.edu.dosw.proyecto.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GrupoRequestDTO {

    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;

    @NotBlank(message = "El ID del horario es obligatorio")
    private String horarioId;

    @NotBlank(message = "El ID de la materia es obligatorio")
    private String materiaId;

    @NotBlank(message = "El ID del periodo académico es obligatorio")
    private String periodoAcademicoId;
}
