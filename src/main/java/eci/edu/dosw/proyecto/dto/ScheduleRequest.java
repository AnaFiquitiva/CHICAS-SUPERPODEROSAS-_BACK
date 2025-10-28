package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScheduleRequest {
    @NotBlank(message = "Día de la semana es requerido")
    private String dayOfWeek;

    @NotBlank(message = "Hora de inicio es requerida")
    private String startTime;

    @NotBlank(message = "Hora de fin es requerida")
    private String endTime;

    private String classroom;

    @NotBlank(message = "Grupo es requerido")
    private String groupId;
}
