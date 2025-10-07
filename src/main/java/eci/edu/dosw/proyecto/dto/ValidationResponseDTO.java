package eci.edu.dosw.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResponseDTO {
    private boolean valid;
    private String message;
    private String details;
}