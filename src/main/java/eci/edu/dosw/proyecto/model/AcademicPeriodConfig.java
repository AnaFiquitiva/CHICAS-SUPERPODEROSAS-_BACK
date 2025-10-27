package eci.edu.dosw.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * * Configuración de períodos académicos para cambios.
        */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "academic_period_configs")
public class AcademicPeriodConfig {
    @Id
    private String id;

    private String periodName; // "Cambios de grupo Semestre 2024-1"
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

    @DBRef
    private Faculty faculty; // Null si es configuración global

    private String allowedRequestTypes; // Tipos de solicitud permitidos

    @DBRef
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Método para verificar si el período está activo
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return active &&
                startDate != null &&
                endDate != null &&
                now.isAfter(startDate) &&
                now.isBefore(endDate);
    }
}