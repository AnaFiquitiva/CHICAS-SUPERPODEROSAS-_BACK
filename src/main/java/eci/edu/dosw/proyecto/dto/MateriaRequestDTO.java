package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MateriaRequestDTO {

    @NotBlank(message = "La descripción de la materia es obligatoria")
    private String descripcion;

    @NotBlank(message = "El ID del periodo académico es obligatorio")
    private String periodoAcademicoId;

    @NotBlank(message = "El código de la materia es obligatorio")
    @Size(min = 3, max = 10, message = "El código debe tener entre 3 y 10 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

}
