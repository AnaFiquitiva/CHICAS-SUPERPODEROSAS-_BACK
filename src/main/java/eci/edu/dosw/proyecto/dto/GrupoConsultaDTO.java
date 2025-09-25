
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
public class GrupoConsultaDTO {
    private String materiaId;
    private String facultad;
    private String departamento;
    private Integer semestre;
    private String profesorId;
    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean soloConCupos;
    private boolean soloActivos;
}
