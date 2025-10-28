package eci.edu.dosw.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Clase que representa un grupo específico de una materia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "groups")
public class Group {
    @Id
    private String id;

    private String groupCode;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Integer totalRequests; // Total de solicitudes para este grupo
    private Integer approvedRequests;
    private Integer rejectedRequests;
    private Integer pendingRequests;
    private LocalDateTime lastOccupancyAlert;

    @DBRef
    private Subject subject;

    @DBRef
    private Professor professor;

    @DBRef
    private List<Schedule> schedules;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Método para verificar disponibilidad
    public boolean hasAvailableSpots() {
        return currentEnrollment < maxCapacity;
    }

    public Double getOccupancyPercentage() {
        // Evita división por cero y maneja capacidades nulas
        if (maxCapacity == null || maxCapacity <= 0) {
            return 0.0;
        }
        return (currentEnrollment.doubleValue() / maxCapacity.doubleValue()) * 100;
    }


    public boolean shouldTrigger90PercentAlert() {
        return getOccupancyPercentage() >= 90.0;
    }

    public void incrementRequestCount(boolean approved) {
        this.totalRequests = (totalRequests == null ? 0 : totalRequests) + 1;
        if (approved) {
            this.approvedRequests = (approvedRequests == null ? 0 : approvedRequests) + 1;
        } else {
            this.rejectedRequests = (rejectedRequests == null ? 0 : rejectedRequests) + 1;
        }
    }



}