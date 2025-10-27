package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String recipientId;
    private String type;
    private String title;
    private String message;
    private String actionUrl;
    private String relatedRequestId;
    private String relatedAlertId;
}
