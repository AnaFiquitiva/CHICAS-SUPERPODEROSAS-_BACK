package eci.edu.dosw.proyecto.model;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente"),
    EN_REVISION("En revisión"),
    APROBADA("Aprobada"),
    RECHAZADA("Rechazada"),
    REQUIERE_INFO("Requiere información adicional");

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
        switch (this) {
            case PENDIENTE:
                return nuevoEstado == EN_REVISION || nuevoEstado == RECHAZADA;
            case EN_REVISION:
                return nuevoEstado == APROBADA || nuevoEstado == RECHAZADA;
            case REQUIERE_INFO:
                return nuevoEstado == EN_REVISION || nuevoEstado == RECHAZADA;
            default:
                return false;
        }
    }
}