package eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestHistoryResponse {
    private String id;
    private RequestStatus previousStatus;
    private RequestStatus newStatus;
    private String comments;
    private String changedByName;
    private LocalDateTime changedAt;
}
