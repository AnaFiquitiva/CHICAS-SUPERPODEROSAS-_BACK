package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "materias")
public class Materia {
    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo;

    private String nombre;
    private String descripcion;
    private Integer creditos;
    private String facultad;
    private String departamento;
    private Integer semestreRecomendado;
    private boolean electiva;

    @Builder.Default
    private boolean activa = true;

    @DBRef
    @Builder.Default
    private List<Materia> prerequisitos = new ArrayList<>();

    @DBRef
    @Builder.Default
    private List<Grupo> grupos = new ArrayList<>();

    public void agregarGrupo(Grupo grupo) {
        if (this.grupos == null) {
            this.grupos = new ArrayList<>();
        }
        this.grupos.add(grupo);
    }

    public void agregarPrerequisito(Materia prerequisito) {
        if (this.prerequisitos == null) {
            this.prerequisitos = new ArrayList<>();
        }
        this.prerequisitos.add(prerequisito);
    }

    public int getNumeroGruposActivos() {
        return grupos != null ? (int) grupos.stream().filter(g -> g != null && g.isActivo()).count() : 0;
    }

    public boolean tieneCuposDisponibles() {
        return grupos != null && grupos.stream()
                .anyMatch(g -> g != null && g.tieneCupo());
    }

    public int getTotalCuposDisponibles() {
        return grupos != null ? grupos.stream()
                .filter(g -> g != null && g.tieneCupo())
                .mapToInt(g -> g.getCupoMaximo() - g.getInscritosActuales())
                .sum() : 0;
    }

    public boolean tienePrerequisitos() {
        return prerequisitos != null && !prerequisitos.isEmpty();
    }

    public boolean esDelSemestre(Integer semestre) {
        return semestreRecomendado != null && semestreRecomendado.equals(semestre);
    }
}