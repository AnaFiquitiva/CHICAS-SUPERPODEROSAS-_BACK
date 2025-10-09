package eci.edu.dosw.proyecto.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentPartialUpdateDTO {

    @Email(message = "El correo personal debe ser válido")
    private String email;

    @Size(max = 100, message = "La dirección no puede superar 100 caracteres")
    private String address;

    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    private String phoneNumber;
}
