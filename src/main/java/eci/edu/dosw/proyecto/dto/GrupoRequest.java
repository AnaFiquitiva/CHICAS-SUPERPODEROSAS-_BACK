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
public class GrupoRequestDTO {

    @NotBlank(message = "El código del grupo es obligatorio")
    @Pattern(regexp = "^[A-Z0-9]{1,5}$", message = "El código debe tener entre 1-5 caracteres alfanuméricos")
    private String codigo;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo máximo debe ser mayor a 0")
    @Max(value = 100, message = "El cupo máximo no puede exceder 100")
    private Integer cupoMaximo;

    private boolean activo = true;

    @Size(max = 10, message = "El aula no puede exceder 10 caracteres")
    private String aula;

    @Size(max = 50, message = "El edificio no puede exceder 50 caracteres")
    private String edificio;

    @NotBlank(message = "El ID del profesor es obligatorio")
    private String profesorId;

    @NotBlank(message = "El ID de la materia es obligatorio")
    private String materiaId;

    @Valid
    @NotEmpty(message = "Debe especificar al menos un horario")
    private List<HorarioRequestDTO> horarios;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoResponseDTO {

    private String id;
    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private Integer disponibilidad;
    private boolean activo;
    private String aula;
    private String edificio;
    private double porcentajeOcupacion;
    private boolean estaCercaDelLimite;
    private boolean estaLleno;

    private ProfesorSummaryDTO profesor;
    private MateriaSummaryDTO materia;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoSummaryDTO {

    private String id;
    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private String aula;
    private String edificio;
    private String materiaCodigoNombre;
    private String profesorNombre;
    private boolean tieneCupo;
}