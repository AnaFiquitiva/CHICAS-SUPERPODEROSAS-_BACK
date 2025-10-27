package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class FacultyResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private boolean active;
}
