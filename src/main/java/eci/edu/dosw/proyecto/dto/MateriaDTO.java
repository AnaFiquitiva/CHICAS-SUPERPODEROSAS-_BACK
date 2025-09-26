package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MateriaDTO {

    @NotBlank(message = "El código de la materia es obligatorio")
    @Size(min = 3, max = 10, message = "El código debe tener entre 3 y 10 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre de la materia es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "El número de créditos es obligatorio")
    private Integer creditos;
}
