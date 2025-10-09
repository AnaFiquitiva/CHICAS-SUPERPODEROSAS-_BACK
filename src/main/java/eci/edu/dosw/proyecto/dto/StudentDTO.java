package eci.edu.dosw.proyecto.dto;

import lombok.Data;

/**
 * DTO principal para la transferencia de datos del Estudiante.
 * Se utiliza para enviar o recibir información desde el cliente.
 */
@Data
public class StudentDTO {
    private String id;
    private String studentCode;
    private String name;
    private String email;
    private String institutionalEmail;
    private String program;
    private Integer currentSemester;
    private String status;
    private String address;      
    private String phoneNumber;
}
