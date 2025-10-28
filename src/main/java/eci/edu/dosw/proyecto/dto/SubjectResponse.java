package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubjectResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private Integer credits;
    private FacultyResponse faculty;
    private List<SubjectBasicResponse> prerequisites;
    private boolean active;
}

