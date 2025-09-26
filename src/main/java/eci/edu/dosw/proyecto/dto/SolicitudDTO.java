package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.EstadoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudDTO {
    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String estudianteId;
    @NotBlank(message = "El ID de la materia destino es obligatorio")
    private String materiaDestinoId;
    @NotNull(message = "El estado inicial es obligatorio")
    private EstadoSolicitud estado;
}
