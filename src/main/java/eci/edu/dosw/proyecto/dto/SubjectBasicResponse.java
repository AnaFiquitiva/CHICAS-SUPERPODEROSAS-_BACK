package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class SubjectBasicResponse {
    private String id;
    private String code;
    private String name;
    private Integer credits;
}
