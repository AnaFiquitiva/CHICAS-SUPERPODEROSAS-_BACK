package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class EstadisticasGrupoDTO {
    private String grupoId;
    private String grupoNombre;
    private Integer totalEstudiantes;
    private Integer cuposDisponibles;
    private Double porcentajeOcupacion;
    private Integer solicitudesRecibidas;
    private Integer solicitudesAprobadas;
    private Integer solicitudesRechazadas;
}