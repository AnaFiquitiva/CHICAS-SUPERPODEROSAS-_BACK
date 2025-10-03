package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitudRequestDTO {

    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String estudianteId;

    @NotBlank(message = "El ID de la materia origen es obligatorio")
    private String materiaOrigenId;

    @NotBlank(message = "El ID del grupo origen es obligatorio")
    private String grupoOrigenId;

    @NotBlank(message = "El ID de la materia destino es obligatorio")
    private String materiaDestinoId;

    @NotBlank(message = "El ID del grupo destino es obligatorio")
    private String grupoDestinoId;

    private String observaciones;
}
