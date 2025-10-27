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
 * Reportes del sistema para estadísticas y análisis.
 * Funcionalidades: 35 (Grupos más solicitados), 36 (Tasa aprobación), 38 (Estadísticas reasignación)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "reports")
public class Report {
    @Id
    private String id;

    private String reportType; // MOST_REQUESTED_GROUPS, APPROVAL_RATES, REASSIGNMENT_STATS, OCCUPANCY_RATES
    private String title;
    private String description;

    @DBRef
    private Faculty faculty; // Filtro por facultad (opcional)

    @DBRef
    private AcademicPeriod academicPeriod; // Período académico del reporte

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Datos del reporte en formato flexible para diferentes tipos
    private Map<String, Object> reportData;
    private String format; // PDF, EXCEL, JSON, CHART

    // Métricas principales
    private Integer totalRequests;
    private Integer approvedRequests;
    private Integer rejectedRequests;
    private Double approvalRate;
    private Double rejectionRate;

    @DBRef
    private User generatedBy;

    private LocalDateTime generatedAt;
    private String filePath; // Ruta al archivo si se exportó

    // Método para calcular tasas
    public void calculateRates() {
        if (totalRequests != null && totalRequests > 0) {
            this.approvalRate = (approvedRequests != null ? approvedRequests.doubleValue() : 0.0) / totalRequests * 100;
            this.rejectionRate = (rejectedRequests != null ? rejectedRequests.doubleValue() : 0.0) / totalRequests * 100;
        }
    }
}
