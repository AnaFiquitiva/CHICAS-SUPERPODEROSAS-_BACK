package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder

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