package eci.edu.dosw.proyecto.dto;


import lombok.Data;

@Data
public class StudentUpdateRequest {
    private String personalEmail;
    private String phone;
    private String address;
}

