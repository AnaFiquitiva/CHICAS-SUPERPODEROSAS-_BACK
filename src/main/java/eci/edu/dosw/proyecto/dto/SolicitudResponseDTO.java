package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class SolicitudResponseDTO {

    private String estudianteId;
    private String materiaOrigenId;
    private String grupoOrigenId;
    private String materiaDestinoId;
    private String grupoDestinoId;
    private String observaciones;

    private String estado; // Estado de la solicitud
    private String mensaje; // Mensaje de respuesta, puede ser de éxito o error
}
