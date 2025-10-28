package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SubjectRequest {
    @NotBlank(message = "Código es requerido")
    private String code;

    @NotBlank(message = "Nombre es requerido")
    private String name;

    @NotBlank(message = "Facultad es requerida")
    private String facultyId;

    @NotNull(message = "Créditos son requeridos")
    @Min(value = 1, message = "Los créditos deben ser al menos 1")
    private Integer credits;

    private String description;
    private List<String> prerequisiteIds;
}
