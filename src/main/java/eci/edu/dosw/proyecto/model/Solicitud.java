package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@Document(collection = "solicitudes")
public class Solicitud {
    @Id
    private String id;

    @Indexed(unique = true)
    private String numeroRadicado;

    private LocalDateTime fechaCreacion;
    private EstadoSolicitud estado;
    private Integer prioridad;
    private String observaciones;
    private String motivoRechazo;
    private String informacionAdicional;

    @DBRef
    private Estudiante estudiante;

    @DBRef
    private Materia materiaOrigen;

    @DBRef
    private Grupo grupoOrigen;

    @DBRef
    private Materia materiaDestino;

    @DBRef
    private Grupo grupoDestino;

    @DBRef
    private Decano decanoAsignado;

    private LocalDateTime fechaRevision;
    private LocalDateTime fechaResolucion;
    private LocalDateTime fechaVencimiento;

    public Solicitud() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoSolicitud.PENDIENTE;
        this.prioridad = 1;
    }

    public boolean isValid() {
        return estudiante != null && materiaOrigen != null && grupoOrigen != null &&
                materiaDestino != null && grupoDestino != null && estado != null;
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (estudiante == null) {
            errors.append("El estudiante es obligatorio. ");
        }
        if (materiaOrigen == null) {
            errors.append("La materia origen es obligatoria. ");
        }
        if (grupoOrigen == null) {
            errors.append("El grupo origen es obligatorio. ");
        }
        if (materiaDestino == null) {
            errors.append("La materia destino es obligatoria. ");
        }
        if (grupoDestino == null) {
            errors.append("El grupo destino es obligatorio. ");
        }
        if (estado == null) {
            errors.append("El estado es obligatorio. ");
        }

        return errors.toString().trim();
    }

    public boolean estaVencida() {
        return fechaVencimiento != null && LocalDateTime.now().isAfter(fechaVencimiento) &&
                !estado.esEstadoFinal();
    }

    public boolean puedeSerModificada() {
        return !estado.esEstadoFinal() && !estaVencida();
    }

    public void cambiarEstado(EstadoSolicitud nuevoEstado, String motivo) {
        if (this.estado.puedeCambiarA(nuevoEstado)) {
            this.estado = nuevoEstado;
            this.motivoRechazo = motivo;

            if (nuevoEstado.esEstadoFinal()) {
                this.fechaResolucion = LocalDateTime.now();
            } else if (nuevoEstado == EstadoSolicitud.EN_REVISION) {
                this.fechaRevision = LocalDateTime.now();
            }
        } else {
            throw new IllegalStateException(
                    String.format("No se puede cambiar del estado %s a %s", estado, nuevoEstado)
            );
        }
    }

    public long getDiasPendientes() {
        if (fechaCreacion == null || estado.esEstadoFinal()) return 0;

        return java.time.Duration.between(fechaCreacion, LocalDateTime.now()).toDays();
    }

    public boolean requiereInformacionAdicional() {
        return estado == EstadoSolicitud.REQUIERE_INFO && informacionAdicional == null;
    }
}