package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EstudianteUpdateDTO {
    private String nombre;
    @Email(message = "El correo no tiene un formato válido")
    private String email;
}
