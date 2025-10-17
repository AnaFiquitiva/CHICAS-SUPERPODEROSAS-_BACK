package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO for subject progress information
 * Represents a subject's status in the student's academic journey
 */
@Data
public class SubjectProgress {
    private String subjectId;
    private String code;
    private String name;
    private Integer credits;
    private String status; // "COMPLETED", "IN_PROGRESS", "PENDING"
    private Integer semester;
    private Double grade; // Optional for completed subjects
}
