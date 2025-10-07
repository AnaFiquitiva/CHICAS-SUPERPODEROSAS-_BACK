package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class EnrollmentRequestDTO {
    @NotNull(message = "El ID del grupo es requerido")
    private String groupId;

    private String observations;
}