package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentScheduleResponse {
    private String id;
    private StudentBasicResponse student;
    private String academicPeriod;
    private Integer academicYear;
    private List<GroupBasicResponse> enrolledGroups;
    private List<ScheduleResponse> schedules;
    private LocalDateTime createdAt;
}
