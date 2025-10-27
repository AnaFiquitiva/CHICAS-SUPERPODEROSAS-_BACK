package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class StudentResponse {
    private String id;
    private String code;
    private String firstName;
    private String lastName;
    private String institutionalEmail;
    private String personalEmail;
    private String phone;
    private String address;
    private ProgramResponse program;
    private Integer currentSemester;
    private String trafficLightColor;
    private boolean active;
}
