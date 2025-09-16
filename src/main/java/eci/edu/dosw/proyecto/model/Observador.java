package eci.edu.dosw.proyecto.model;

/**
 * Interface Observer para notificaciones
 */
public interface Observador {
    /**
     * Método llamado cuando el sujeto observado cambia
     */
    void actualizar();
}