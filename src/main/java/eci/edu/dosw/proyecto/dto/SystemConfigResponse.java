package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemConfigResponse {
    private String id;
    private Integer defaultMaxGroupCapacity;
    private Double occupancyAlertThreshold;
    private Integer maxWaitingListSize;
    private boolean allowSpecialApprovals;
    private Integer sessionTimeoutMinutes;
    private Integer maxAcademicLoad;
    private Integer minAcademicLoad;
    private LocalDateTime currentRequestPeriodStart;
    private LocalDateTime currentRequestPeriodEnd;
    private UserBasicResponse lastModifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

