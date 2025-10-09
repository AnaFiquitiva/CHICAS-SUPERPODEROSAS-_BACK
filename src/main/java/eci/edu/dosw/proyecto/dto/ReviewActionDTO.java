package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class ReviewActionDTO {
    private String employeeCode;
    private String comments;
    private String reason;
    private String requiredInformation;
}