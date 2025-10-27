package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GroupDemandResponse {
    private String groupId;
    private String groupCode;
    private String subjectName;
    private Integer totalRequests;
    private Integer approvedRequests;
    private Double occupancyPercentage;
    private Integer demandRank;
    private Integer waitingListCount;
}

