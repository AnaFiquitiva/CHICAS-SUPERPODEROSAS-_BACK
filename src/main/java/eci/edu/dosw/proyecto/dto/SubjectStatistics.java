package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class SubjectStatistics {
    private String subjectId;
    private String subjectName;
    private Integer totalRequests;
    private Integer approved;
    private String mostCommonSourceGroup;
    private String mostCommonTargetGroup;
}
