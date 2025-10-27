package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Data
public class SystemConfigRequest {
    @NotNull(message = "Capacidad máxima por defecto es requerida")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer defaultMaxGroupCapacity;

    @NotNull(message = "Umbral de alerta es requerido")
    @DecimalMin(value = "0.1", message = "El umbral debe ser entre 0.1 y 1.0")
    @DecimalMax(value = "1.0", message = "El umbral debe ser entre 0.1 y 1.0")
    private Double occupancyAlertThreshold;

    @NotNull(message = "Tamaño máximo de lista de espera es requerido")
    @Min(value = 1, message = "El tamaño debe ser al menos 1")
    private Integer maxWaitingListSize;

    private Boolean allowSpecialApprovals;
    private Integer sessionTimeoutMinutes;
    private Integer maxAcademicLoad;
    private Integer minAcademicLoad;
    private LocalDateTime currentRequestPeriodStart;
    private LocalDateTime currentRequestPeriodEnd;
}

