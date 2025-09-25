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
@Document(collection = "profesores")
public class Profesor extends Usuario {

    private String departamento;
    private String especialidad;

    @DBRef
    private List<Grupo> gruposAsignados = new ArrayList<>();

    public Profesor() {
        super();
        this.setRol(RolUsuario.PROFESOR);
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                departamento != null && !departamento.trim().isEmpty();
    }

    @Override
    public String getValidationErrors() {
        String parentErrors = super.getValidationErrors();
        StringBuilder errors = new StringBuilder(parentErrors);

        if (departamento == null || departamento.trim().isEmpty()) {
            errors.append("El departamento es obligatorio. ");
        }

        return errors.toString().trim();
    }

    public void asignarGrupo(Grupo grupo) {
        if (this.gruposAsignados == null) {
            this.gruposAsignados = new ArrayList<>();
        }
        this.gruposAsignados.add(grupo);
    }

    public boolean tieneGrupoAsignado(Grupo grupo) {
        return gruposAsignados.stream()
                .anyMatch(g -> g.getId().equals(grupo.getId()));
    }

    public int getNumeroGrupos() {
        return gruposAsignados != null ? gruposAsignados.size() : 0;
    }
}