package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacultyRequest {
    @NotBlank(message = "Código es requerido")
    private String code;

    @NotBlank(message = "Nombre es requerido")
    private String name;

    private String description;
}
