package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SolicitudDetalleDTO {
    private String id;
    private String numeroRadicado;
    private LocalDateTime fechaCreacion;
    private String estado;
    private Integer prioridad;
    private String observaciones;
    private EstudianteInfoDTO estudiante;
    private MateriaInfoDTO materiaOrigen;
    private GrupoInfoDTO grupoOrigen;
    private MateriaInfoDTO materiaDestino;
    private GrupoInfoDTO grupoDestino;
    private String decanoAsignado;
    private LocalDateTime fechaRevision;
    private LocalDateTime fechaResolucion;
}