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
public class HorarioRequestDTO {

    @NotNull(message = "El día es obligatorio")
    private DiaSemana dia;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @Size(max = 10, message = "El aula no puede exceder 10 caracteres")
    private String aula;

    @Size(max = 50, message = "El edificio no puede exceder 50 caracteres")
    private String edificio;

    @AssertTrue(message = "La hora de inicio debe ser anterior a la hora de fin")
    public boolean isValidTimeRange() {
        if (horaInicio == null || horaFin == null) return true;
        return horaInicio.isBefore(horaFin);
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HorarioResponseDTO {

    private String id;
    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String aula;
    private String edificio;
    private long duracionMinutos;
    private String horarioFormateado;
}


