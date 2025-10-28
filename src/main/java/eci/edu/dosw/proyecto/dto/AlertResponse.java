package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertResponse {
    private String id;
    private String type;
    private String title;
    private String description;
    private String severity;
    private GroupBasicResponse group;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private UserBasicResponse resolvedBy;
    private LocalDateTime createdAt;
}
