package eci.edu.dosw.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la creación de solicitudes de cambio de horario
 */
@Schema(description = "Datos requeridos para crear una solicitud de cambio de horario")
public class SolicitudRequest {

    @NotBlank(message = "El ID del estudiante es requerido")
    @Schema(description = "ID o código del estudiante que realiza la solicitud",
            example = "1234567890", required = true)
    private String idEstudiante;

    @NotBlank(message = "El código de la materia de origen es requerido")
    @Schema(description = "Código de la materia actual del estudiante",
            example = "MAT101", required = true)
    private String codigoMateriaOrigen;

    @Schema(description = "Código del grupo actual del estudiante (opcional)",
            example = "G1")
    private String codigoGrupoOrigen;

    @NotBlank(message = "El código de la materia de destino es requerido")
    @Schema(description = "Código de la materia a la que desea cambiar",
            example = "PROG201", required = true)
    private String codigoMateriaDestino;

    @NotBlank(message = "El código del grupo de destino es requerido")
    @Schema(description = "Código del grupo al que desea cambiar",
            example = "G2", required = true)
    private String codigoGrupoDestino;

    @Schema(description = "Observaciones o justificación del cambio",
            example = "Conflicto de horarios con otra materia")
    private String observaciones;

    // Getters y Setters en español
    public String getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(String idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getCodigoMateriaOrigen() { return codigoMateriaOrigen; }
    public void setCodigoMateriaOrigen(String codigoMateriaOrigen) { this.codigoMateriaOrigen = codigoMateriaOrigen; }

    public String getCodigoGrupoOrigen() { return codigoGrupoOrigen; }
    public void setCodigoGrupoOrigen(String codigoGrupoOrigen) { this.codigoGrupoOrigen = codigoGrupoOrigen; }

    public String getCodigoMateriaDestino() { return codigoMateriaDestino; }
    public void setCodigoMateriaDestino(String codigoMateriaDestino) { this.codigoMateriaDestino = codigoMateriaDestino; }

    public String getCodigoGrupoDestino() { return codigoGrupoDestino; }
    public void setCodigoGrupoDestino(String codigoGrupoDestino) { this.codigoGrupoDestino = codigoGrupoDestino; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}