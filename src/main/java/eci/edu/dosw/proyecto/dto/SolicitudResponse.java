package eci.edu.dosw.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * DTO para la respuesta de solicitudes de cambio de horario
 */
@Schema(description = "Respuesta con los detalles de una solicitud de cambio de horario")
public class SolicitudResponse {

    @Schema(description = "Identificador único de la solicitud",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Nombre completo del estudiante", example = "Juan Pérez")
    private String nombreEstudiante;

    @Schema(description = "Nombre de la materia de origen", example = "Matemáticas Básicas")
    private String materiaOrigen;

    @Schema(description = "Nombre de la materia de destino", example = "Programación I")
    private String materiaDestino;

    @Schema(description = "Estado actual de la solicitud",
            example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "EN_REVISION", "APROBADO", "RECHAZADO"})
    private String estado;

    @Schema(description = "Fecha y hora de creación de la solicitud",
            example = "2024-01-15T10:30:00")
    private Date fechaCreacion;

    @Schema(description = "Observaciones ingresadas por el estudiante",
            example = "Conflicto de horarios con otra materia")
    private String observaciones;

    @Schema(description = "Prioridad de la solicitud calculada", example = "895")
    private Integer prioridad;

    // Getters y Setters en español
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getMateriaOrigen() { return materiaOrigen; }
    public void setMateriaOrigen(String materiaOrigen) { this.materiaOrigen = materiaOrigen; }

    public String getMateriaDestino() { return materiaDestino; }
    public void setMateriaDestino(String materiaDestino) { this.materiaDestino = materiaDestino; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }
}