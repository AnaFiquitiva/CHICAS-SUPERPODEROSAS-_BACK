package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HorarioDTO {
    @NotBlank(message = "El día es obligatorio")
    private String dia;
    @NotNull(message = "La hora de inicio es obligatoria")
    private String horaInicio;
    @NotNull(message = "La hora de fin es obligatoria")
    private String horaFin;
    @NotNull(message = "Debe asociar un grupo")
    private String grupoId;
}
