package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private UserResponse user;
    private String role;
    private LocalDateTime expiresAt;
}
