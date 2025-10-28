package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WaitingListEntryResponse {
    private String id;
    private Integer position;
    private StudentBasicResponse student;
    private LocalDateTime joinedAt;
    private boolean active;
}
