package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OccupancyReportResponse {
    private String facultyId;
    private String facultyName;
    private Integer totalGroups;
    private Integer fullGroups;
    private Integer highOccupancyGroups;
    private Integer availableGroups;
    private Double averageOccupancy;
    private List<GroupCapacityResponse> groupDetails;
    private LocalDateTime generatedAt;
}
