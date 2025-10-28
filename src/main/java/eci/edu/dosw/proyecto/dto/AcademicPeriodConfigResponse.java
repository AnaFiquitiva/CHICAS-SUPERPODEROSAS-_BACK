package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AcademicPeriodConfigResponse {
    private String id;
    private String periodName;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private FacultyBasicResponse faculty;
    private String allowedRequestTypes;
    private UserBasicResponse createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
