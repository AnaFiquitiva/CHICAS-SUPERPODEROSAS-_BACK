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

    public boolean isValid() {
        return codigo != null && !codigo.trim().isEmpty() &&
                nombre != null && !nombre.trim().isEmpty() &&
                creditos != null && creditos >= 1 &&
                facultad != null && !facultad.trim().isEmpty();
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (codigo == null || codigo.trim().isEmpty()) {
            errors.append("El código de la materia es obligatorio. ");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            errors.append("El nombre de la materia es obligatorio. ");
        }
        if (creditos == null || creditos < 1) {
            errors.append("Los créditos deben ser mayor a 0. ");
        }
        if (facultad == null || facultad.trim().isEmpty()) {
            errors.append("La facultad es obligatoria. ");
        }

        return errors.toString().trim();
    }

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
