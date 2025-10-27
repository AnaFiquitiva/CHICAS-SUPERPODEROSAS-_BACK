package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GroupCapacityResponse {
    private String groupId;
    private String groupCode;
    private String subjectName;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Integer availableSpots;
    private Double occupancyPercentage;
    private boolean hasAvailableSpots;
    private Integer waitingListCount;
}
