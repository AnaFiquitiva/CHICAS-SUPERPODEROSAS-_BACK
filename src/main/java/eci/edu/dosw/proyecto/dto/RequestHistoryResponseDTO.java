package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestHistoryResponseDTO {
    private LocalDateTime timestamp;
    private String action;
    private String description;
    private String userId;
    private String userRole;
}