package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubjectProgressResponse {
    private String id;
    private SubjectBasicResponse subject;
    private String status; // PENDING, IN_PROGRESS, APPROVED, FAILED
    private Double grade;
    private Integer attempt;
    private String academicPeriod;
    private String trafficLightColor; // RED, YELLOW, GREEN
    private boolean canBeRetaken;
    private LocalDateTime completedAt;
}

