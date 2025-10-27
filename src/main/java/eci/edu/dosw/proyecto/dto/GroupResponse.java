package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupResponse {
    private String id;
    private String groupCode;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Double occupancyPercentage;
    private boolean hasAvailableSpots;
    private SubjectBasicResponse subject;
    private ProfessorBasicResponse professor;
    private List<ScheduleResponse> schedules;
    private boolean active;
}
