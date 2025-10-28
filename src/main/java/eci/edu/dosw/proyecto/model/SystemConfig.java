package eci.edu.dosw.proyecto.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

/**
 * Configuración global del sistema para parámetros ajustables.
 * Funcionalidades: 26 (Monitoreo 90%), 25 (Períodos), 30 (Modificación cupos)
 */
@Data
@AllArgsConstructor
@Builder
@Document(collection = "system_config")
public class SystemConfig {
    @Id
    private String id;

    private Integer defaultMaxGroupCapacity;
    private Double occupancyAlertThreshold; // 0.9 para 90%
    private Integer maxWaitingListSize;
    private boolean allowSpecialApprovals;
    private Integer sessionTimeoutMinutes; // Para autenticación
    private Integer maxAcademicLoad; // Máxima carga académica permitida
    private Integer minAcademicLoad; // Mínima carga académica requerida

    // Configuración de períodos (Funcionalidad 25)
    private LocalDateTime currentRequestPeriodStart;
    private LocalDateTime currentRequestPeriodEnd;
    private Boolean active;

    @DBRef
    private User lastModifiedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SystemConfig() {
        this.active = true;
    }

}
