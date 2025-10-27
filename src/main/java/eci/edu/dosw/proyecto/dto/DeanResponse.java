package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class DeanResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String institutionalEmail;
    private String personalEmail;
    private String phone;
    private FacultyResponse faculty;
    private boolean active;
}
