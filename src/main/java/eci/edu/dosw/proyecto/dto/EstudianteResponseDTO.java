package src.main.java.eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class EstudianteResponseDTO {
    private String estudianteId;
    private String nombre;
    private String codigo;
    private String email;
    private String estado;

    private String status; // Estado o mensaje de la operación
    private String mensaje; // Mensaje de éxito o error
}
