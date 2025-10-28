package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestStatsResponse {
    private String facultyId;
    private String facultyName;
    private Integer totalRequests;
    private Integer pendingRequests;
    private Integer approvedRequests;
    private Integer rejectedRequests;
    private Integer additionalInfoRequests;
    private Integer cancelledRequests;
    private Double averageProcessingTime; // en horas
    private Double approvalRate;
    private Double rejectionRate;
    private LocalDateTime calculatedAt;
}
