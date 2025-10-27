package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class ScheduleResponse {
    private String id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String classroom;
    private String groupId;
}