package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ReassignmentStatsResponse {
    private String period;
    private String facultyId;
    private String facultyName;
    private Integer totalReassignments;
    private Integer successfulReassignments;
    private Integer failedReassignments;
    private Double successRate;
    private Map<String, Integer> reassignmentsByFaculty;
    private Map<String, Integer> reassignmentsBySubject;
    private Map<String, Integer> reassignmentsByType;
    private LocalDateTime generatedAt;}
