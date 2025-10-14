package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
