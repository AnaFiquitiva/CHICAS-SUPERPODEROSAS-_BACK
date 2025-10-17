package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCapacityResponseDTO {
    private String groupId;
    private String groupCode;
    private String subjectName;
    private Integer currentEnrollment;
    private Integer maxCapacity;
    private Integer availableSpaces;
    private Integer waitlistCount;
    private Double occupancyRate;
    private String status; // "AVAILABLE", "FULL", "LIMITED"

    // Constructor opcional para casos específicos
    public GroupCapacityResponseDTO(String groupId, String groupCode, Integer currentEnrollment,
                                    Integer maxCapacity, Integer waitlistCount) {
        this.groupId = groupId;
        this.groupCode = groupCode;
        this.currentEnrollment = currentEnrollment;
        this.maxCapacity = maxCapacity;
        this.availableSpaces = maxCapacity - currentEnrollment;
        this.waitlistCount = waitlistCount;
        this.occupancyRate = maxCapacity > 0 ? (double) currentEnrollment / maxCapacity * 100 : 0.0;
        this.status = determineStatus();
        this.subjectName = "Subject-" + groupId; // Placeholder
    }

    private String determineStatus() {
        if (availableSpaces <= 0) {
            return "FULL";
        } else if (availableSpaces <= 2) {
            return "LIMITED";
        } else {
            return "AVAILABLE";
        }
    }
}