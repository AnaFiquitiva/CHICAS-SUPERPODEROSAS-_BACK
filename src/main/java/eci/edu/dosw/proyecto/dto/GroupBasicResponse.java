package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GroupBasicResponse {
    private String id;
    private String groupCode;
    private String subjectName;
    private String subjectCode;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Double occupancyPercentage;
    private boolean hasAvailableSpots;
}
