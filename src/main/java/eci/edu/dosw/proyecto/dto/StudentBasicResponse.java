package eci.edu.dosw.proyecto.dto;


import lombok.Data;

@Data
public class StudentBasicResponse {
    private String id;
    private String code;
    private String firstName;
    private String lastName;
    private String institutionalEmail;
    private String programName;
    private Integer currentSemester;
    private String trafficLightColor;
}