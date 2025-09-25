package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "periodos_academicos")
public class PeriodoAcademico {
    @Id
    private String id;

    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean activo;
    private String descripcion;
    private LocalDateTime fechaInicioSolicitudes;
    private LocalDateTime fechaFinSolicitudes;

    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() &&
                fechaInicio != null && fechaFin != null &&
                fechaInicio.isBefore(fechaFin);
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (nombre == null || nombre.trim().isEmpty()) {
            errors.append("El nombre del período es obligatorio. ");
        }
        if (fechaInicio == null) {
            errors.append("La fecha de inicio es obligatoria. ");
        }
        if (fechaFin == null) {
            errors.append("La fecha de fin es obligatoria. ");
        }
        if (fechaInicio != null && fechaFin != null && !fechaInicio.isBefore(fechaFin)) {
            errors.append("La fecha de inicio debe ser antes de la fecha fin. ");
        }

        return errors.toString().trim();
    }

    public boolean estaActivo() {
        LocalDateTime ahora = LocalDateTime.now();
        return activo && ahora.isAfter(fechaInicio) && ahora.isBefore(fechaFin);
    }

    public boolean periodoSolicitudesActivo() {
        LocalDateTime ahora = LocalDateTime.now();
        return fechaInicioSolicitudes != null && fechaFinSolicitudes != null &&
                ahora.isAfter(fechaInicioSolicitudes) && ahora.isBefore(fechaFinSolicitudes);
    }

    public boolean periodoSolicitudesFuturo() {
        return fechaInicioSolicitudes != null && LocalDateTime.now().isBefore(fechaInicioSolicitudes);
    }

    public boolean periodoSolicitudesPasado() {
        return fechaFinSolicitudes != null && LocalDateTime.now().isAfter(fechaFinSolicitudes);
    }

    public long getDiasRestantesSolicitudes() {
        if (fechaFinSolicitudes == null) return 0;
        return java.time.Duration.between(LocalDateTime.now(), fechaFinSolicitudes).toDays();
    }

    public String getEstadoPeriodoSolicitudes() {
        if (periodoSolicitudesActivo()) return "ACTIVO";
        if (periodoSolicitudesFuturo()) return "FUTURO";
        if (periodoSolicitudesPasado()) return "PASADO";
        return "NO_CONFIGURADO";
    }
}
