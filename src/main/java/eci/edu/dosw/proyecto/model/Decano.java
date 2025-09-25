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
@Document(collection = "decanos")
public class Decano extends Usuario {

    private String facultad;
    private String departamento;
    private String telefonoOficina;

    @DBRef
    private List<Materia> materiasGestionadas = new ArrayList<>();

    @DBRef
    private List<Solicitud> solicitudesAsignadas = new ArrayList<>();

    public Decano() {
        super();
        this.rol = eci.edu.dosw.proyecto.model.RolUsuario.DECANO;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                facultad != null && !facultad.trim().isEmpty();
    }

    @Override
    public String getValidationErrors() {
        String parentErrors = super.getValidationErrors();
        StringBuilder errors = new StringBuilder(parentErrors);

        if (facultad == null || facultad.trim().isEmpty()) {
            errors.append("La facultad es obligatoria. ");
        }

        return errors.toString().trim();
    }

    public void asignarSolicitud(Solicitud solicitud) {
        if (this.solicitudesAsignadas == null) {
            this.solicitudesAsignadas = new ArrayList<>();
        }
        this.solicitudesAsignadas.add(solicitud);
        solicitud.setDecanoAsignado(this);
    }

    public boolean gestionaMateria(Materia materia) {
        return materiasGestionadas.stream()
                .anyMatch(m -> m.getId().equals(materia.getId()));
    }

    public long getSolicitudesPendientes() {
        return solicitudesAsignadas.stream()
                .filter(s -> s.getEstado() == eci.edu.dosw.proyecto.model.EstadoSolicitud.PENDIENTE ||
                        s.getEstado() == eci.edu.dosw.proyecto.model.EstadoSolicitud.EN_REVISION)
                .count();
    }
}