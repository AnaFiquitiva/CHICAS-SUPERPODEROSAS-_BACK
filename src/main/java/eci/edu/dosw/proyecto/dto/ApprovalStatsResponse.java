package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalStatsResponse {
    private String facultyId;
    private String facultyName;
    private Integer totalRequests;
    private Integer approvedRequests;
    private Integer rejectedRequests;
    private Integer pendingRequests;
    private Double approvalRate;
    private Double rejectionRate;
    private LocalDateTime calculatedAt;

    public void setAdditionalInfoRequests(Integer additionalInfoRequests) {

    }

    public void setCancelledRequests(Integer cancelledRequests) {

    }
}
