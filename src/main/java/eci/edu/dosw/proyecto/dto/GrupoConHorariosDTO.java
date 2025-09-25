
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
public class GrupoConHorariosDTO {

    private String id;
    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private boolean activo;
    private String aula;
    private String edificio;

    private ProfesorSummaryDTO profesor;
    private MateriaSummaryDTO materia;
    private List<HorarioResponseDTO> horarios;
    private List<InscripcionSummaryDTO> inscripcionesActivas;
}