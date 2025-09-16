package eci.edu.dosw.proyecto.model;

/**
 * Estado Rechazado de una solicitud
 */
public class Rechazado implements EstadoSolicitud {
    private SolicitudCambio solicitud;

    public Rechazado(SolicitudCambio solicitud) {
        this.solicitud = solicitud;
    }

    @Override
    public void procesar() {
        System.out.println("Solicitud rechazada: " + solicitud.getId());
        // No se puede procesar más allá de este punto
    }

    @Override
    public boolean puedeAprobar() {
        return false; // Ya está rechazada
    }

    @Override
    public String toString() {
        return "Rechazado";
    }
}
