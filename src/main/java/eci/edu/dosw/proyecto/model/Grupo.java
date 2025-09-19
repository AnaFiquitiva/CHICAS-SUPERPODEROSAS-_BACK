package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un grupo específico de una materia
 */

@Document(collection = "grupos")
public class Grupo {
    @Id
    private String codigo;
    private Materia materia;
    private String profesor;
    private String horario;
    private Integer cupoMaximo;
    private Integer cupoActual;
    private List<Estudiante> estudiantesInscritos;

    public Grupo(String codigo, Materia materia, String profesor, String horario, Integer cupoMaximo) {
        this.codigo = codigo;
        this.materia = materia;
        this.profesor = profesor;
        this.horario = horario;
        this.cupoMaximo = cupoMaximo;
        this.cupoActual = 0;
        this.estudiantesInscritos = new ArrayList<>();
    }

    /**
     * Agrega un estudiante al grupo si hay cupo disponible
     */
    public void agregarEstudiante(Estudiante estudiante) {
        if (verificarDisponibilidad()) {
            estudiantesInscritos.add(estudiante);
            cupoActual++;
        } else {
            throw new RuntimeException("No hay cupo disponible en el grupo " + codigo);
        }
    }

    /**
     * Remueve un estudiante del grupo
     */
    public void removerEstudiante(Estudiante estudiante) {
        if (estudiantesInscritos.remove(estudiante)) {
            cupoActual--;
        }
    }

    /**
     * Verifica si hay cupo disponible en el grupo
     */
    public boolean verificarDisponibilidad() {
        return cupoActual < cupoMaximo;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Integer getCupoActual() {
        return cupoActual;
    }

    public List<Estudiante> getEstudiantesInscritos() {
        return estudiantesInscritos;
    }

    public void setEstudiantesInscritos(List<Estudiante> estudiantesInscritos) {
        this.estudiantesInscritos = estudiantesInscritos;
        this.cupoActual = estudiantesInscritos.size();
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "codigo='" + codigo + '\'' +
                ", materia=" + (materia != null ? materia.getNombre() : "N/A") +
                ", profesor='" + profesor + '\'' +
                ", horario='" + horario + '\'' +
                ", cupoMaximo=" + cupoMaximo +
                ", cupoActual=" + cupoActual +
                '}';
    }
}
