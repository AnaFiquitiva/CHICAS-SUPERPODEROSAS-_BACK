package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfessorRequest {
    @NotBlank(message = "Código es requerido")
    private String code;

    @NotBlank(message = "Nombre es requerido")
    private String firstName;

    @NotBlank(message = "Apellido es requerido")
    private String lastName;

    @NotBlank(message = "Correo institucional es requerido")
    @Email(message = "Correo debe ser válido")
    private String institutionalEmail;

    @NotBlank(message = "Facultad es requerida")
    private String facultyId;
}
