package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

/**
 * DTO completo para representar un profesor.
 * Se utiliza para crear, actualizar o consultar la información de un profesor.
 */
@Data
public class ProfessorDTO {

    /** Identificador único del profesor (obligatorio). */
    @NotBlank
    private String id;

    /** Nombre completo del profesor (obligatorio). */
    @NotBlank
    private String name;

    /** Número de identificación del profesor (obligatorio). */
    @NotBlank
    private String identification;

    /** Correo institucional del profesor (obligatorio y válido). */
    @NotBlank
    @Email
    private String institutionalEmail;

    /** Correo personal del profesor (opcional y válido). */
    @Email
    private String email;

    /** Teléfono de contacto del profesor (opcional). */
    private String phone;

    /** Dirección del profesor (opcional). */
    private String address;

    /** ID de la facultad a la que pertenece el profesor. */
    private String facultyId;

    /** Lista de IDs de materias asignadas al profesor. */
    private List<String> subjectIds;

    /** Estado activo o inactivo del profesor. */
    private boolean active;
}
