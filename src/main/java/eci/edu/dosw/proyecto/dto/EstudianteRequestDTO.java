package src.main.java.eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EstudianteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El código del estudiante es obligatorio")
    private String codigo;

    @Email(message = "El correo no tiene un formato válido")
    private String email;

    private String estudianteId;
}
