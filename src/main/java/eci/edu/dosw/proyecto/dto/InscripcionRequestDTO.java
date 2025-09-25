
package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import eci.edu.dosw.proyecto.model.*;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionRequestDTO {

    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String estudianteId;

    @NotBlank(message = "El ID del grupo es obligatorio")
    private String grupoId;
}
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionResponseDTO {

    private String id;
    private LocalDateTime fechaInscripcion;
    private boolean activa;
    private Double calificacion;
    private String estadoAcademico;
    private LocalDateTime fechaCancelacion;
    private String estado;
    private boolean esDelPeriodoActual;

    private EstudianteSummaryDTO estudiante;
    private GrupoSummaryDTO grupo;
}
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionSummaryDTO {

    private String id;
    private LocalDateTime fechaInscripcion;
    private boolean activa;
    private Double calificacion;
    private String estado;
    private String materiaCodigoNombre;
    private String grupoCodigo;
    private String profesorNombre;
}
