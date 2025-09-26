package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String userId;
    private String email;
    private String role;
    private String nombre;
    private boolean success;
    private String message;

    public AuthenticationResponseDTO(boolean b, String usuarioInactivo) {
    }
}
