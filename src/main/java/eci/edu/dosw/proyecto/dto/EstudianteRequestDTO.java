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
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class EstudianteRequestDTO extends UsuarioBaseDTO {

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "La carrera es obligatoria")
    private String carrera;

    @NotNull(message = "El semestre es obligatorio")
    @Min(value = 1, message = "El semestre debe ser mayor a 0")
    @Max(value = 12, message = "El semestre no puede ser mayor a 12")
    private Integer semestre;

    @DecimalMin(value = "0.0", message = "El promedio no puede ser negativo")
    @DecimalMax(value = "5.0", message = "El promedio no puede ser mayor a 5.0")
    private Double promedioAcumulado;
}
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class EstudianteResponseDTO extends UsuarioBaseDTO {

    private String carrera;
    private Integer semestre;
    private SemaforoAcademico semaforo;
    private Double promedioAcumulado;
    private int numeroInscripciones;
    private int numeroSolicitudes;
    private boolean puedeSolicitarCambio;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteSummaryDTO {
    private String id;
    private String codigo;
    private String nombre;
    private String carrera;
    private Integer semestre;
    private SemaforoAcademico semaforo;
}