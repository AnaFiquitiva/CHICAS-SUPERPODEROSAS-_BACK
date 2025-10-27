package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class UserBasicResponse {
    private String id;
    private String username;
    private String email;
    private String role;
}
