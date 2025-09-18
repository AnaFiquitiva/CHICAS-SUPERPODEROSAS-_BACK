package eci.edu.dosw.proyecto.model;

import java.util.Objects;

/**
 * Clase base para todos los usuarios del sistema
 */
public abstract class Usuario implements Observador {
    protected String id;
    protected String codigo;
    protected String nombre;
    protected String email;
    protected String rol;
    protected String facultad;

    public Usuario(String id, String codigo, String nombre, String email, String rol, String facultad) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.facultad = facultad;
    }

    /**
     * Autentica al usuario con credenciales institucionales
     */
    public boolean autenticar(String password) {
        // TODO: Implementar la lógica real de autenticación
        return password != null && !password.isEmpty();
    }

    @Override
    public void actualizar() {
        System.out.println("Usuario " + nombre + " ha sido notificado de cambios");
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", facultad='" + facultad + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
