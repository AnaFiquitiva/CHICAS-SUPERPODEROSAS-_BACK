package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase que representa un estudiante del sistema
 */


public class Estudiante extends Usuario {
    private String carrera;
    private Integer semestre;
    private List<Materia> historial;
    private String semaforoEstado;


    public Estudiante(String id, String codigo, String nombre, String email, String facultad,
                      String carrera, Integer semestre) {
        super(id, codigo, nombre, email, Roles.ESTUDIANTE, facultad);
        this.carrera = carrera;
        this.semestre = semestre;
        this.historial = new ArrayList<>();

        // Inicializar semáforo
        actualizarSemaforoEstado();
    }

    /**
     * Método privado para actualizar el estado del semáforo basado en el historial actual
     */
    private void actualizarSemaforoEstado() {
        RendimientoSemaforo rendimientoSemaforo = new RendimientoSemaforo(historial, semestre);
        this.semaforoEstado = rendimientoSemaforo.calcularEstado();
    }

    /**
     * Crea una nueva solicitud de cambio de horario
     * MÉTODO CORREGIDO - Coincide con el constructor de SolicitudCambio
     */
    public SolicitudCambio crearSolicitud(Materia materiaOrigen, Grupo grupoOrigen,
                                          Materia materiaDestino, Grupo grupoDestino,
                                          String observaciones) {
        // Validaciones previas
        if (!validarPeriodoSolicitudes()) {
            throw new RuntimeException("No está en período de solicitudes");
        }

        if (yaCancoloMateria(materiaOrigen)) {
            throw new RuntimeException("No se puede solicitar cambio para materia ya cancelada");
        }

        return new SolicitudCambio(
                UUID.randomUUID().toString(),
                this,
                materiaOrigen,
                grupoOrigen,
                materiaDestino,
                grupoDestino,
                observaciones
        );
    }

    /**
     * Método simplificado para crear solicitud - Para mantener compatibilidad
     */
    public SolicitudCambio crearSolicitud(Materia materiaOrigen, Grupo grupoDestino, String observaciones) {
        return crearSolicitud(materiaOrigen, null, materiaOrigen, grupoDestino, observaciones);
    }

    private boolean validarPeriodoSolicitudes() {
        // Lógica para validar si está en período de solicitudes
        return true; // Simplificado
    }

    private boolean yaCancoloMateria(Materia materia) {
        // Lógica para verificar si ya canceló la materia en el semestre actual
        return false; // Simplificado
    }

    // Getters y Setters
    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
        // Actualizar semáforo cuando cambia el semestre
        actualizarSemaforoEstado();
    }

    public List<Materia> getHistorial() {
        return historial;
    }

    public void setHistorial(List<Materia> historial) {
        this.historial = historial;
        // CORRECCIÓN PRINCIPAL: Recalcular semáforo cuando se actualiza el historial
        actualizarSemaforoEstado();
    }

    public String getSemaforoEstado() {
        return semaforoEstado;
    }

    public void setSemaforoEstado(String semaforoEstado) {
        this.semaforoEstado = semaforoEstado;
    }

    @Override
    public void actualizar() {
        System.out.println("Estudiante " + nombre + " ha sido notificado de cambios");
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", carrera='" + carrera + '\'' +
                ", semestre=" + semestre +
                ", facultad='" + facultad + '\'' +
                ", semaforoEstado='" + semaforoEstado + '\'' +
                '}';
    }
}