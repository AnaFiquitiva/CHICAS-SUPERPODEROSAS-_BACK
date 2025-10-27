package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestResponse {
    private String id;
    private String requestNumber;
    private RequestType type;
    private RequestStatus status;
    private String description;
    private String observations;
    private String justification;
    private boolean specialApproval;
    private String specialApprovalJustification;

    private StudentBasicResponse student;
    private GroupBasicResponse currentGroup;
    private GroupBasicResponse requestedGroup;
    private SubjectBasicResponse requestedSubject;

    private UserBasicResponse processedBy;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
