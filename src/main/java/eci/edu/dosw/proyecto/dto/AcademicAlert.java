package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO for academic alerts
 * Represents specific academic concerns that need attention
 */
@Data
public class AcademicAlert {
    private String type; // "PERFORMANCE", "ATTENDANCE", "PENDING_SUBJECTS"
    private String severity; // "LOW", "MEDIUM", "HIGH"
    private String description;
    private String recommendedAction;
}
