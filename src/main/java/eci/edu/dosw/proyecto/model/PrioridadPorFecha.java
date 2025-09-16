package eci.edu.dosw.proyecto.model;

import java.util.Date;

/**
 * Estrategia de prioridad que asigna prioridad según la fecha de creación
 * Cuanto más reciente la solicitud, menor prioridad (orden de llegada).
 */
public class PrioridadPorFecha implements Prioridad {
    private Date fechaCreacion;

    public PrioridadPorFecha(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public int calcularPrioridad() {
        // Prioridad más alta para solicitudes más antiguas (FIFO)
        // Cuanto menor sea el tiempo en milisegundos, mayor prioridad
        long tiempo = fechaCreacion.getTime();
        return (int) (Long.MAX_VALUE - tiempo);
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "PrioridadPorFecha{" +
                "fechaCreacion=" + fechaCreacion +
                ", prioridad=" + calcularPrioridad() +
                '}';
    }
}
