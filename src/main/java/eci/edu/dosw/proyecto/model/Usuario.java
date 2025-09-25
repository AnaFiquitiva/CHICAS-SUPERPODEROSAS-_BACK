package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "usuarios")
public abstract class Usuario {
    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nombre;

    @Indexed(unique = true)
    private String email;

    private String password;

    private RolUsuario rol;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoAcceso;

    // Métodos de validación manuales
    public boolean isValid() {
        return codigo != null && !codigo.trim().isEmpty() &&
                nombre != null && nombre.length() >= 2 && nombre.length() <= 100 &&
                email != null && isValidEmail(email) &&
                password != null && password.length() >= 6;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public void actualizarUltimoAcceso() {
        this.fechaUltimoAcceso = LocalDateTime.now();
    }

    public boolean esActivo() {
        return activo && (fechaUltimoAcceso == null ||
                fechaUltimoAcceso.isAfter(LocalDateTime.now().minusMonths(6)));
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        if (codigo == null || codigo.trim().isEmpty()) {
            errors.append("El código es obligatorio. ");
        }
        if (nombre == null || nombre.length() < 2 || nombre.length() > 100) {
            errors.append("El nombre debe tener entre 2 y 100 caracteres. ");
        }
        if (email == null || !isValidEmail(email)) {
            errors.append("El formato del email no es válido. ");
        }
        if (password == null || password.length() < 6) {
            errors.append("La contraseña debe tener al menos 6 caracteres. ");
        }
        return errors.toString().trim();
    }
}