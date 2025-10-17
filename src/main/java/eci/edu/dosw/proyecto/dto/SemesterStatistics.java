package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO for semester statistics
 * Contains quantitative academic performance metrics
 */
@Data
public class SemesterStatistics {
    private Integer enrolledSubjects;
    private Integer passedSubjects;
    private Integer failedSubjects;
    private Integer withdrawnSubjects;
    private Double semesterAverage;
    private Integer attendances;
    private Integer absences;
    private Double attendancePercentage;
}
