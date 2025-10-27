package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class ProfessorBasicResponse {
    private String id;
    private String code;
    private String firstName;
    private String lastName;
    private String institutionalEmail;
}
