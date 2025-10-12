package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDto {
    private String courseName;
    private String day;
    private String startTime;
    private String endTime;
    private String classroom;
    private String semesterCode;
}