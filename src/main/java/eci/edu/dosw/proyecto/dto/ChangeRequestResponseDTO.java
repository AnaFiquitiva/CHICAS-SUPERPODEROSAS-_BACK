package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChangeRequestResponseDTO {
    private String id;
    private String requestNumber;
    private String studentId;
    private LocalDateTime creationDate;
    private RequestType type;
    private RequestStatus status;
    private Integer priority;
    private String currentSubjectId;
    private String currentGroupId;
    private String targetSubjectId;
    private String targetGroupId;
    private List<PlanChangeDetailResponseDTO> planChanges;
    private String observations;
    private List<RequestHistoryResponseDTO> history;
}