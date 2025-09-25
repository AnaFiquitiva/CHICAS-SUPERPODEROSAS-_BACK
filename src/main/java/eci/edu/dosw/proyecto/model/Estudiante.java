package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "estudiantes")
public class Estudiante extends Usuario {

    private String carrera;
    private Integer semestre;
    private SemaforoAcademico semaforo;
    private Double promedioAcumulado;

    @DBRef
    private List<Inscripcion> inscripciones = new ArrayList<>();

    @DBRef
    private List<Solicitud> solicitudes = new ArrayList<>();

    public Estudiante() {
        super();
        this.rol = RolUsuario.ESTUDIANTE;
        this.semaforo = SemaforoAcademico.AZUL;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                carrera != null && !carrera.trim().isEmpty() &&
                semestre != null && semestre >= 1;
    }

    @Override
    public String getValidationErrors() {
        String parentErrors = super.getValidationErrors();
        StringBuilder errors = new StringBuilder(parentErrors);

        if (carrera == null || carrera.trim().isEmpty()) {
            errors.append("La carrera es obligatoria. ");
        }
        if (semestre == null || semestre < 1) {
            errors.append("El semestre debe ser mayor a 0. ");
        }

        return errors.toString().trim();
    }

    public void agregarInscripcion(Inscripcion inscripcion) {
        if (this.inscripciones == null) {
            this.inscripciones = new ArrayList<>();
        }
        this.inscripciones.add(inscripcion);
    }

    public boolean tieneInscripcionActiva(Grupo grupo) {
        return inscripciones.stream()
                .anyMatch(ins -> ins.getGrupo().getId().equals(grupo.getId()) && ins.isActiva());
    }

    public List<Grupo> obtenerGruposInscritos() {
        return inscripciones.stream()
                .filter(Inscripcion::isActiva)
                .map(Inscripcion::getGrupo)
                .toList();
    }

    public List<Horario> obtenerHorarios() {
        return inscripciones.stream()
                .filter(Inscripcion::isActiva)
                .map(Inscripcion::getGrupo)
                .flatMap(grupo -> grupo.getHorarios().stream())
                .toList();
    }

    public void actualizarSemaforo(double nuevoPromedio) {
        this.promedioAcumulado = nuevoPromedio;
        this.semaforo = SemaforoAcademico.fromPromedio(nuevoPromedio);
    }

    public boolean puedeSolicitarCambio() {
        return this.semaforo != SemaforoAcademico.ROJO ||
                this.solicitudes.stream().noneMatch(s -> s.getEstado() == eci.edu.dosw.proyecto.model.EstadoSolicitud.PENDIENTE);
    }
}