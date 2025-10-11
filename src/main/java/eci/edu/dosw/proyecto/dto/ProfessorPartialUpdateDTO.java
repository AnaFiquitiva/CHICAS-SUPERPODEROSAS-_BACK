package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * DTO para actualización parcial de un profesor.
 * Solo permite modificar correo personal, teléfono y dirección.
 */
@Data
public class ProfessorPartialUpdateDTO {

    /** Correo personal del profesor (opcional y válido). */
    @Email
    private String email;

    /** Teléfono de contacto del profesor (opcional). */
    private String phone;

    /** Dirección del profesor (opcional). */
    private String address;
}
