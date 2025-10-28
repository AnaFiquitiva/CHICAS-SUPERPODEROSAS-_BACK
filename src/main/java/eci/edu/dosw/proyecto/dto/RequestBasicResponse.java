package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestBasicResponse {
    private String id;
    private String requestNumber;
    private RequestType type;
    private RequestStatus status;
    private String studentName;
    private String studentCode;
    private String programName;
    private LocalDateTime createdAt;
    private Integer priorityScore;
}
