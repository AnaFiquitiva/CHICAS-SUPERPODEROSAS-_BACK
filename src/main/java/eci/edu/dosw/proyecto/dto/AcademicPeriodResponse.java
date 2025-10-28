package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AcademicPeriodResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private LocalDateTime createdAt;
}