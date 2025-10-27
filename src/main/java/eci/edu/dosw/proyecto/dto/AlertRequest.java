package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class AlertRequest {
    private String type;
    private String title;
    private String description;
    private String severity;
    private String groupId;
}
