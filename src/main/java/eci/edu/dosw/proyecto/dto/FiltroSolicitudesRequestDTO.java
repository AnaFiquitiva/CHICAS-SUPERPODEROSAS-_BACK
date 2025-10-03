package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class FiltroSolicitudesRequestDTO {
    private String estadoSolicitud;
    private String periodoAcademicoId;
}
