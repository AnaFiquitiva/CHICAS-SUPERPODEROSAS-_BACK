package eci.edu.dosw.proyecto.model;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
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
    private boolean activa;

    @DBRef
    private List<Materia> prerequisitos = new ArrayList<>();

    @DBRef
    private List<Grupo> grupos = new ArrayList<>();


    public void agregarGrupo(Grupo grupo) {
        if (this.grupos == null) {
            this.grupos = new ArrayList<>();
        }
        this.grupos.add(grupo);
    }

    public int getNumeroGruposActivos() {
        return grupos != null ? (int) grupos.stream().filter(Grupo::isActivo).count() : 0;
    }

    public boolean tieneCuposDisponibles() {
        return grupos.stream().anyMatch(Grupo::tieneCupo);
    }
}
