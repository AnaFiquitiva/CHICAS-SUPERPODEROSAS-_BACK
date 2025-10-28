package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private String id;
    private String type;
    private String title;
    private String message;
    private String actionUrl;
    private boolean read;
    private UserBasicResponse recipient;
    private RequestBasicResponse relatedRequest;
    private AlertResponse relatedAlert;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}