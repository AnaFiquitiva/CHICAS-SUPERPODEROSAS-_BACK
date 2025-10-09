package eci.edu.dosw.proyecto.dto;


import lombok.Data;

/**
 * DTO para actualización parcial de estudiante por el propio estudiante.
 * Permite modificar solo correo personal, dirección y teléfono.
 */
@Data
public class StudentPartialUpdateDTO {
    private String email;       // correo personal
    private String address;     // dirección
    private String phoneNumber; // teléfono
}