package eci.edu.dosw.proyecto.model;

/**
 * Estado Pendiente de una solicitud
 */
public class Pendiente implements EstadoSolicitud {
    private SolicitudCambio solicitud;

    public Pendiente(SolicitudCambio solicitud) {
        this.solicitud = solicitud;
    }

    @Override
    public void procesar() {
        System.out.println("Procesando solicitud pendiente: " + solicitud.getId());
        // Aquí podría ir lógica de validaciones previas antes de pasar a otro estado
    }

    @Override
    public boolean puedeAprobar() {
        return true; // Las solicitudes pendientes pueden ser aprobadas
    }

    @Override
    public String toString() {
        return "Pendiente";
    }
}
