package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentCreateDTO {

    @NotBlank(message = "El código del estudiante es obligatorio")
    private String studentCode;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Email(message = "El correo institucional debe ser válido")
    @NotBlank(message = "El correo institucional es obligatorio")
    private String institutionalEmail;

    @NotBlank(message = "El programa es obligatorio")
    private String program;

    @NotNull(message = "El semestre actual es obligatorio")
    private Integer currentSemester;

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phoneNumber;
}
