package eci.edu.dosw.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una materia
 */

@Document(collection = "materias")
public class Materia {
    @Id
    private String codigo;
    private String nombre;
    private Integer creditos;
    private String facultad;
    private boolean aprobada;
    private List<Grupo> grupos;

    public Materia(String codigo, String nombre, Integer creditos, String facultad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.creditos = creditos;
        this.facultad = facultad;
        this.grupos = new ArrayList<>();
        this.aprobada = false;
    }

    public Materia() {
        this.grupos = new ArrayList<>();
        this.aprobada = false;
    }

    /**
     * Obtiene todos los grupos disponibles para esta materia
     */
    public List<Grupo> obtenerGrupos() {
        return new ArrayList<>(grupos);
    }

    public void agregarGrupo(Grupo grupo) {
        grupos.add(grupo);
    }

    // Getters y setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public boolean isAprobada() {
        return aprobada;
    }

    public void setAprobada(boolean aprobada) {
        this.aprobada = aprobada;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", creditos=" + creditos +
                ", facultad='" + facultad + '\'' +
                ", aprobada=" + aprobada +
                ", grupos=" + grupos.size() +
                '}';
    }
}
