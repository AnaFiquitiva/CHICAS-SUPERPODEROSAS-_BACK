package eci.edu.dosw.proyecto.model;

/**
 * Factory para crear diferentes tipos de usuarios
 */
public class UsuarioFactory {

    /**
     * Crea un estudiante
     */
    public static Estudiante crearEstudiante(String id, String codigo, String nombre, String email,
                                             String facultad, String carrera, Integer semestre) {
        return new Estudiante(id, codigo, nombre, email, facultad, carrera, semestre);
    }

    /**
     * Crea una decanatura
     */
    public static Decanatura crearDecanatura(String id, String codigo, String nombre, String email,
                                             String facultad) {
        return new Decanatura(id, codigo, nombre, email, facultad);
    }

    /**
     * Crea un administrador
     */
    public static Administrador crearAdministrador(String id, String codigo, String nombre, String email,
                                                   String facultad) {
        return new Administrador(id, codigo, nombre, email, facultad);
    }
}
