package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String role;
    private boolean active;
    private LocalDateTime lastLogin;
}

