package eci.edu.dosw.proyecto.model;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@Document(collection = "grupos")
public class Grupo {
    @Id
    private String id;

    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private boolean activo;
    private String aula;
    private String edificio;

    @DBRef
    private Profesor profesor;

    @DBRef
    private Materia materia;

    @DBRef
    private List<Horario> horarios = new ArrayList<>();

    @DBRef
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Grupo() {
        this.capacidadActual = 0;
        this.activo = true;
    }

    public boolean isValid() {
        return codigo != null && !codigo.trim().isEmpty() &&
                cupoMaximo != null && cupoMaximo >= 1 &&
                capacidadActual != null && capacidadActual >= 0 &&
                materia != null;
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (codigo == null || codigo.trim().isEmpty()) {
            errors.append("El código del grupo es obligatorio. ");
        }
        if (cupoMaximo == null || cupoMaximo < 1) {
            errors.append("El cupo máximo debe ser mayor a 0. ");
        }
        if (capacidadActual == null || capacidadActual < 0) {
            errors.append("La capacidad actual no puede ser negativa. ");
        }
        if (materia == null) {
            errors.append("El grupo debe estar asociado a una materia. ");
        }

        return errors.toString().trim();
    }

    public Integer getDisponibilidad() {
        return cupoMaximo - capacidadActual;
    }

    public boolean tieneCupo() {
        return capacidadActual < cupoMaximo;
    }

    public boolean estaCercaDelLimite() {
        return capacidadActual >= (cupoMaximo * 0.9);
    }

    public boolean estaLleno() {
        return capacidadActual >= cupoMaximo;
    }

    public double getPorcentajeOcupacion() {
        return (double) capacidadActual / cupoMaximo * 100;
    }

    public void incrementarCapacidad() {
        if (this.capacidadActual < this.cupoMaximo) {
            this.capacidadActual++;
        } else {
            throw new IllegalStateException("El grupo ya está lleno");
        }
    }

    public void decrementarCapacidad() {
        if (this.capacidadActual > 0) {
            this.capacidadActual--;
        }
    }

    public void agregarHorario(Horario horario) {
        if (this.horarios == null) {
            this.horarios = new ArrayList<>();
        }
        this.horarios.add(horario);
    }

    public boolean tieneCruceHorario(Grupo otroGrupo) {
        for (Horario h1 : this.horarios) {
            for (Horario h2 : otroGrupo.horarios) {
                if (h1.hayCruce(h2)) {
                    return true;
                }
            }
        }
        return false;
    }
}