package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Contraseña actual es requerida")
    private String currentPassword;

    @NotBlank(message = "Nueva contraseña es requerida")
    @Size(min = 10, message = "La contraseña debe tener al menos 10 caracteres")
    private String newPassword;
}
