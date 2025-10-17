/**
 * DTO for academic traffic light system response
 * Provides comprehensive academic status overview with alerts and recommendations
 */
package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.util.List;

@Data
public class AcademicTrafficLightResponse {
    private String studentId;
    private String studentName;
    private String program;
    private Integer currentSemester;
    private String status; // "GREEN", "YELLOW", "RED"
    private Double performanceIndex;
    private List<AcademicAlert> alerts;
    private Recommendations recommendations;
    private SemesterStatistics statistics;
}

