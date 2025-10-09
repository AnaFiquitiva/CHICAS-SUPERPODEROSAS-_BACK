package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO utilizado exclusivamente para la creación de nuevos estudiantes.
 * Contiene los campos requeridos para el registro.
 */
@Data
public class StudentCreateDTO {
    private String studentCode;
    private String name;
    private String institutionalEmail;
    private String program;
    private Integer currentSemester;
    private String status;
    private String address;
    private String phoneNumber;
}
