package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
/**
 * Clase que representa el progreso de una materia específica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "subject_progress")
public class SubjectProgress {
    @Id
    private String id;

    @DBRef
    private Subject subject;

    private String status; // PENDING, IN_PROGRESS, APPROVED, FAILED
    private Double grade;
    private Integer attempt;
    private String academicPeriod;
    private LocalDateTime completedAt;
    public String getTrafficLightColor() {
        return switch (status) {
            case "PENDING" -> "GRAY";      // No cursada
            case "IN_PROGRESS" -> "YELLOW"; // En curso
            case "APPROVED" -> "GREEN";    // Aprobada
            case "FAILED" -> "RED";        // Reprobada
            default -> "GRAY";
        };

    }
    public boolean canBeRetaken() {
        // Se puede recursar si está reprobada (< 3.0) y tiene menos de 3 intentos
        return "FAILED".equals(status) &&
                grade != null &&
                grade < 3.0 &&
                (attempt == null || attempt < 3);
    }


}
