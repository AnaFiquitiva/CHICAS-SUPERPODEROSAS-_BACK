package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class FiltroSolicitudesResponseDTO {
    private String estadoSolicitud;
    private String periodoAcademicoId;
    private int totalSolicitudes;

    private String status;  // Estado de la operación
    private String mensaje; // Mensaje de éxito o error
}
