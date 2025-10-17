package eci.edu.dosw.proyecto.dto;

import lombok.Data;

// Supporting DTO classes
@Data
public class GlobalStatistics {
    private Integer totalRequests;
    private Integer approvedRequests;
    private Integer rejectedRequests;
    private Integer pendingRequests;
    private Double approvalRate;
    private Integer groupChanges;
    private Integer subjectChanges;
    private Integer planChanges;
}
