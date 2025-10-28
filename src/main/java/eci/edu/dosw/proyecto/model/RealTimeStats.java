package eci.edu.dosw.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.Map;
/**
 * Estadísticas en tiempo real para dashboards.
 * Funcionalidades: 35, 36, 38 (Varios reportes)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "real_time_stats")
public class RealTimeStats {
    @Id
    private String id;

    private String statType; // REQUESTS_BY_FACULTY, OCCUPANCY_BY_GROUP, APPROVAL_RATES
    private LocalDateTime calculatedAt;

    @DBRef
    private Faculty faculty; // Para estadísticas por facultad

    @DBRef
    private AcademicPeriod academicPeriod;

    // Datos estadísticos
    private Map<String, Object> statistics;
    private Map<String, Integer> counts;
    private Map<String, Double> percentages;

    private LocalDateTime createdAt;

    // Método para actualizar estadísticas
    public void updateCalculationTime() {
        this.calculatedAt = LocalDateTime.now();
    }
}
