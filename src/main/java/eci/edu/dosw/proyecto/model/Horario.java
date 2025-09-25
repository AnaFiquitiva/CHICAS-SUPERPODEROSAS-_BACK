package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "horarios")
public class Horario {
    @Id
    private String id;

    private DiaSemana dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String aula;
    private String edificio;

    public boolean isValid() {
        return dia != null && horaInicio != null && horaFin != null &&
                horaInicio.isBefore(horaFin);
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (dia == null) {
            errors.append("El día es obligatorio. ");
        }
        if (horaInicio == null) {
            errors.append("La hora de inicio es obligatoria. ");
        }
        if (horaFin == null) {
            errors.append("La hora de fin es obligatoria. ");
        }
        if (horaInicio != null && horaFin != null && !horaInicio.isBefore(horaFin)) {
            errors.append("La hora de inicio debe ser antes de la hora de fin. ");
        }

        return errors.toString().trim();
    }

    public boolean hayCruce(Horario otro) {
        if (this.dia != otro.dia) return false;

        return this.horaInicio.isBefore(otro.horaFin) &&
                this.horaFin.isAfter(otro.horaInicio);
    }

    public boolean esValido() {
        return horaInicio != null && horaFin != null && horaInicio.isBefore(horaFin);
    }

    public long getDuracionMinutos() {
        return java.time.Duration.between(horaInicio, horaFin).toMinutes();
    }

    public String getHorarioFormateado() {
        return String.format("%s %s-%s", dia.getNombre(),
                horaInicio.toString(), horaFin.toString());
    }
}
