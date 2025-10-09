package eci.edu.dosw.proyecto.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExceptionalCaseHistory {
    private LocalDateTime timestamp;
    private String action;
    private String description;
    private String userId;
    private String userRole;
    private String comments;
}