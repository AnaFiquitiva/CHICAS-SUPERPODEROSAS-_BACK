package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAssignmentRequest {
    @NotBlank(message = "ID del usuario es requerido")
    private String userId;

    @NotBlank(message = "ID del nuevo rol es requerido")
    private String newRoleId;
}