package eci.edu.dosw.proyecto.model;

public enum RolUsuario {
    ESTUDIANTE("Estudiante"),
    PROFESOR("Profesor"),
    DECANO("Decano"),
    ADMINISTRADOR("Administrador");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean puedeGestionarSolicitudes() {
        return this == DECANO || this == ADMINISTRADOR;
    }

    public boolean puedeConfigurarSistema() {
        return this == ADMINISTRADOR;
    }

    public boolean puedeVerReportes() {
        return this != ESTUDIANTE;
    }
}