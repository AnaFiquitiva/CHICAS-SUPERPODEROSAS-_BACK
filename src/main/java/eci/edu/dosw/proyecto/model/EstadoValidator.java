package eci.edu.dosw.proyecto.model;


/**
 * Utilidades para validar estados de solicitudes
 */
public final class EstadoValidator {
    
    public static boolean esEstadoValido(String estado) {
        return EstadosSolicitud.PENDIENTE.equals(estado) ||
               EstadosSolicitud.APROBADO.equals(estado) ||
               EstadosSolicitud.RECHAZADO.equals(estado) ||
               EstadosSolicitud.EN_REVISION.equals(estado);
    }
    
    public static boolean puedeTransicionarA(String estadoActual, String nuevoEstado) {
        // Reglas de transición de estados
        return switch (estadoActual) {
            case EstadosSolicitud.PENDIENTE -> EstadosSolicitud.APROBADO.equals(nuevoEstado) ||
                    EstadosSolicitud.RECHAZADO.equals(nuevoEstado) ||
                    EstadosSolicitud.EN_REVISION.equals(nuevoEstado);
            case EstadosSolicitud.EN_REVISION -> EstadosSolicitud.APROBADO.equals(nuevoEstado) ||
                    EstadosSolicitud.RECHAZADO.equals(nuevoEstado);
            default -> false; // Estados finales no pueden cambiar
        };
    }
    
    private EstadoValidator() {} // Utility class
}