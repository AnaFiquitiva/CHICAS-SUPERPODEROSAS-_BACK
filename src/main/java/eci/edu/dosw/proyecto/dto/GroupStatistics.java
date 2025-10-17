package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GroupStatistics {
    private String groupId;
    private String groupCode;
    private Integer outgoingRequests;
    private Integer incomingRequests;
    private Integer capacity;
    private Integer currentOccupancy;
}
