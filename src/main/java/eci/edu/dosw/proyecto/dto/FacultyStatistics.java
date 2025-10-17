package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class FacultyStatistics {
    private String faculty;
    private Integer totalRequests;
    private Integer approved;
    private Double approvalRate;
    private String mostRequestedSubject;
}
