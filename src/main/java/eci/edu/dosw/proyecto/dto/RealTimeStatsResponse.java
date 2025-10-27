package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class RealTimeStatsResponse {
    private String statType;
    private String facultyId;
    private String facultyName;
    private LocalDateTime calculatedAt;
    private Map<String, Object> statistics;
    private Map<String, Integer> counts;
    private Map<String, Double> percentages;
    private String description;
}
