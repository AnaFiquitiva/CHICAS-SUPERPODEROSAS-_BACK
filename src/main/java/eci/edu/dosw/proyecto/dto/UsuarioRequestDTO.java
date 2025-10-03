package src.main.java.eci.edu.dosw.proyecto.dto;

import eci.edu.dosw.proyecto.model.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String passwordConfirmation;

    @NotNull(message = "El rol es obligatorio")
    private RolUsuario role;
}
