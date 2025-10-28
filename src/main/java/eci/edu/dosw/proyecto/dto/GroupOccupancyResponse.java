// eci.edu.dosw.proyecto.dto.GroupOccupancyResponse.java
package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GroupOccupancyResponse {
    private String groupId;
    private String groupCode;
    private String subjectName;
    private String facultyName;
    private Integer maxCapacity;
    private Integer currentEnrollment;
    private Double occupancyPercentage;
    private Integer waitingListCount;
    private boolean hasAvailableSpots;
}