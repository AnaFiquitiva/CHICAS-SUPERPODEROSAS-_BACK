package eci.edu.dosw.proyecto.model;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor

@Document(collection = "inscripciones")
public class Inscripcion {
    @Id
    private String id;

    private LocalDateTime fechaInscripcion;
    private boolean activa;
    private Double calificacion;
    private String estadoAcademico;

    @DBRef
    private Estudiante estudiante;

    @DBRef
    private Grupo grupo;

    private LocalDateTime fechaCancelacion;

    public Inscripcion() {
        this.fechaInscripcion = LocalDateTime.now();
        this.activa = true;
    }

    public boolean isValid() {
        return estudiante != null && grupo != null && fechaInscripcion != null;
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (estudiante == null) {
            errors.append("El estudiante es obligatorio. ");
        }
        if (grupo == null) {
            errors.append("El grupo es obligatorio. ");
        }
        if (fechaInscripcion == null) {
            errors.append("La fecha de inscripción es obligatoria. ");
        }

        return errors.toString().trim();
    }

    public void cancelar() {
        this.activa = false;
        this.fechaCancelacion = LocalDateTime.now();
        if (this.grupo != null) {
            this.grupo.decrementarCapacidad();
        }
    }

    public void reactivar() {
        if (!this.activa && this.grupo.tieneCupo()) {
            this.activa = true;
            this.fechaCancelacion = null;
            this.grupo.incrementarCapacidad();
        }
    }

    public boolean esDelPeriodoActual() {
        return fechaInscripcion.isAfter(LocalDateTime.now().minusMonths(6));
    }

    public String getEstado() {
        if (!activa) return "Cancelada";
        if (calificacion != null) return "Calificada";
        return "Activa";
    }
}