package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudDTO {
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

    // El estado no debe venir del front, se asigna automáticamente
    // private EstadoSolicitud estado;
}