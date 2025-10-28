package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRequest {
    @NotBlank(message = "Código es requerido")
    private String code;

    @NotBlank(message = "Nombre es requerido")
    private String firstName;

    @NotBlank(message = "Apellido es requerido")
    private String lastName;

    @NotBlank(message = "Correo institucional es requerido")
    @Email(message = "Correo institucional debe ser válido")
    private String institutionalEmail;

    @NotBlank(message = "Programa es requerido")
    private String programId;

    @NotNull(message = "Semestre es requerido")
    @Min(value = 1, message = "Semestre debe ser mayor a 0")
    private Integer currentSemester;
}
