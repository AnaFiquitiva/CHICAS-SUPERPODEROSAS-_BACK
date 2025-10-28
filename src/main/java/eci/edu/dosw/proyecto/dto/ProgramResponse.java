package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class ProgramResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private FacultyResponse faculty;
    private Integer totalCredits;
    private Integer durationSemesters;
    private boolean active;
}
