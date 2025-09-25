package eci.edu.dosw.proyecto.model;

public enum TipoNotificacion {
    CAMBIO_ESTADO_SOLICITUD("Cambio de estado de solicitud"),
    ALERTA_CUPO("Alerta de cupo"),
    RECORDATORIO_REVISION("Recordatorio de revisión"),
    NOTICIA_SISTEMA("Noticia del sistema"),
    CONFIRMACION_CAMBIO("Confirmación de cambio");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
