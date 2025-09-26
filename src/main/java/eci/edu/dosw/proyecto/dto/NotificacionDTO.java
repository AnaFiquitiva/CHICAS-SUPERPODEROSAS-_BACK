package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificacionDTO {
    @NotBlank(message = "El ID del destinatario es obligatorio")
    private String destinatarioId;
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;
    @NotNull(message = "El tipo de notificación es obligatorio")
    private TipoNotificacion tipo;
}
