package eci.edu.dosw.proyecto.model;

/**
 * Clase que representa un administrador del sistema
 */
public class Administrador extends Usuario {

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
