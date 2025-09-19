package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * Clase que representa un administrador del sistema
 */

@Document(collection = "administradores")
public class Administrador extends Usuario {
    @Id
    private String id;

    public Administrador(String id, String codigo, String nombre, String email, String facultad) {
        super(id, codigo, nombre, email, "ADMINISTRADOR", facultad);
    }

    @Override
    public void actualizar() {
        System.out.println("Administrador " + nombre + " ha sido notificado de cambios");
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "id='" + id + '\'' +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", facultad='" + facultad + '\'' +
                '}';
    }
}
