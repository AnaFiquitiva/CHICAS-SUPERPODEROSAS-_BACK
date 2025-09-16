package eci.edu.dosw.proyecto.model;

/**
 * Estado Aprobado de una solicitud
 */
public class Aprobado implements EstadoSolicitud {
    private SolicitudCambio solicitud;

    public Aprobado(SolicitudCambio solicitud) {
        this.solicitud = solicitud;
    }

    @Override
    public void procesar() {
        System.out.println("Solicitud ya aprobada: " + solicitud.getId());
        // Aquí se podría ejecutar la lógica de inscripción al grupo destino
    }

    @Override
    public boolean puedeAprobar() {
        return false; // Ya está aprobada
    }

    @Override
    public String toString() {
        return "Aprobado";
    }
}
