package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SolicitudUpdateDTO {
    @NotNull(message = "El estado actualizado es obligatorio")
    private EstadoSolicitud estado;
}
