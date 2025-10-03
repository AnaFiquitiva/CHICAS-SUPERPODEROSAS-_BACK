package eci.edu.dosw.proyecto.model;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente"),
    EN_REVISION("En revisión"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    REQUIERE_INFO("Requiere información adicional"),
    CANCELADA("Se canceló la solicitud");


    private final String descripcion;

    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esEstadoFinal() {
        return this == APROBADA || this == RECHAZADA;
    }

    public boolean puedeCambiarA(EstadoSolicitud nuevoEstado) {
        return switch (this) {
            case PENDIENTE -> nuevoEstado == EN_REVISION || nuevoEstado == CANCELADA || nuevoEstado == REQUIERE_INFO;
            case EN_REVISION -> nuevoEstado == APROBADA || nuevoEstado == RECHAZADA || nuevoEstado == REQUIERE_INFO;
            case REQUIERE_INFO -> nuevoEstado == EN_REVISION || nuevoEstado == CANCELADA;
            case APROBADA, RECHAZADA, CANCELADA -> false; // Estados finales no pueden cambiar
        };
    }
}