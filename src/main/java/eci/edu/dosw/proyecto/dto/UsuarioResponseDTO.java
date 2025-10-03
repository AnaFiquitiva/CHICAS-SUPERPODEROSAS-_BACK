package src.main.java.eci.edu.dosw.proyecto.dto;


import eci.edu.dosw.proyecto.model.RolUsuario;
import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private String username;
    private String email;
    private RolUsuario role;

    // Campos adicionales para la respuesta
    private String token; // Si es un DTO de autenticación, el token de la respuesta
    private String status; // Estado de la respuesta
    private String mensaje; // Mensaje de éxito o error
}
