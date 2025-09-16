package eci.edu.dosw.proyecto.model;

import java.util.Date;

/**
 * Clase que representa una solicitud de cambio de horario
 */
public class SolicitudCambio extends Sujeto {
    private String id;
    private Estudiante estudiante;
    private Materia materiaOrigen;
    private Grupo grupoOrigen;
    private Materia materiaDestino;
    private Grupo grupoDestino;
    private String observaciones;
    private Date fechaCreacion;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private String estadoString;

    public SolicitudCambio(String id, Estudiante estudiante, Materia materiaOrigen,
                           Grupo grupoOrigen, Materia materiaDestino, Grupo grupoDestino,
                           String observaciones) {
        this.id = id;
        this.estudiante = estudiante;
        this.materiaOrigen = materiaOrigen;
        this.grupoOrigen = grupoOrigen;
        this.materiaDestino = materiaDestino;
        this.grupoDestino = grupoDestino;
        this.observaciones = observaciones;
        this.fechaCreacion = new Date();

        // Asignar prioridad automática por orden de llegada
        this.prioridad = new PrioridadPorFecha(fechaCreacion);

        // Estado inicial: Pendiente
        this.estado = new Pendiente(this);
        this.estadoString = EstadosSolicitud.PENDIENTE;
    }

    /**
     * Actualiza el estado de la solicitud
     */
    public void actualizarEstado(EstadoSolicitud nuevoEstado, String estadoStr) {
        this.estado = nuevoEstado;
        this.estadoString = estadoStr;
        notificar(); // Notificar a observadores sobre el cambio de estado
    }

    /**
     * Valida si hay cupo disponible en el grupo destino
     */
    public boolean validarCupo() {
        return grupoDestino.verificarDisponibilidad();
    }

    /**
     * Valida si hay cruce de horarios
     */
    public boolean validarCruceHorarios() {
        // Lógica para validar cruces de horario
        // En una implementación real, esto compararía horarios detalladamente
        return true; // Simplificado
    }
    public void cambiarEstado(EstadoSolicitud nuevoEstado, String estadoStr) {
        if (EstadoValidator.puedeTransicionarA(this.estadoString, estadoStr)) {
            this.estado = nuevoEstado;
            this.estadoString = estadoStr;
            notificar(); // Notificar a observadores
        } else {
            throw new RuntimeException("Transición de estado no permitida: " +
                    this.estadoString + " -> " + estadoStr);
        }
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public void setEstadoString(String estadoString) {
        this.estadoString = estadoString;
    }

    // Getters
    public String getId() { return id; }
    public Estudiante getEstudiante() { return estudiante; }
    public Materia getMateriaOrigen() { return materiaOrigen; }
    public Grupo getGrupoOrigen() { return grupoOrigen; }
    public Materia getMateriaDestino() { return materiaDestino; }
    public Grupo getGrupoDestino() { return grupoDestino; }
    public String getObservaciones() { return observaciones; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public Prioridad getPrioridad() { return prioridad; }
    public EstadoSolicitud getEstado() { return estado; }
    public String getEstadoString() { return estadoString; }
}
