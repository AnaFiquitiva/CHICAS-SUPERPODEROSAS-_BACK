package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AcademicTrafficLightResponse {
    private String color;
    private String description;
    private String message;
    private Double currentGpa;
    private Integer completedCredits;
    private Integer totalCredits;
    private Double progressPercentage;
    private LocalDateTime calculatedAt;
}
