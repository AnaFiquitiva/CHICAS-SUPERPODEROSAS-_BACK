package eci.edu.dosw.proyecto.model;

/**
 * Interface para el patrón State - Estados de solicitud
 */
public interface EstadoSolicitud {

    /**
     * Procesa la solicitud según su estado actual
     */
    void procesar();

    /**
     * Verifica si la solicitud puede ser aprobada
     * @return true si puede ser aprobada
     */
    boolean puedeAprobar();

    /**
     * Representación en texto del estado actual
     * @return nombre del estado
     */
    @Override
    String toString();
}
