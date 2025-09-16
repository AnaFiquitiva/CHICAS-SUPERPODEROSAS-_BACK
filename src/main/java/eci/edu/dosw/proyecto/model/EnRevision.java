package eci.edu.dosw.proyecto.model;

/**
 * Estado En Revisión de una solicitud
 */
public class EnRevision implements EstadoSolicitud {
    private SolicitudCambio solicitud;

    public EnRevision(SolicitudCambio solicitud) {
        this.solicitud = solicitud;
    }

    @Override
    public void procesar() {
        System.out.println("Solicitud en revisión: " + solicitud.getId());
        // Aquí podrías implementar lógica extra:
        // - Verificación de documentos
        // - Validación por otro departamento
    }

    @Override
    public boolean puedeAprobar() {
        // Una solicitud en revisión todavía puede ser aprobada
        return true;
    }

    @Override
    public String toString() {
        return "En Revisión";
    }
}
