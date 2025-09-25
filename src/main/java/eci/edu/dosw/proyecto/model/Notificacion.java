package eci.edu.dosw.proyecto.model;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@Document(collection = "notificaciones")
public class Notificacion {
    @Id
    private String id;

    private TipoNotificacion tipo;
    private String titulo;
    private String mensaje;

    @DBRef
    private Usuario destinatario;

    private boolean leida;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaLectura;
    private String urlAccion;
    private boolean enviadaPorEmail;

    public Notificacion() {
        this.fechaEnvio = LocalDateTime.now();
        this.leida = false;
    }

    public boolean isValid() {
        return tipo != null && titulo != null && !titulo.trim().isEmpty() &&
                mensaje != null && !mensaje.trim().isEmpty() && destinatario != null;
    }

    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();

        if (tipo == null) {
            errors.append("El tipo de notificación es obligatorio. ");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            errors.append("El título es obligatorio. ");
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            errors.append("El mensaje es obligatorio. ");
        }
        if (destinatario == null) {
            errors.append("El usuario destinatario es obligatorio. ");
        }

        return errors.toString().trim();
    }

    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }

    public boolean esReciente() {
        return fechaEnvio.isAfter(LocalDateTime.now().minusDays(7));
    }

    public boolean requiereAccion() {
        return urlAccion != null && !leida;
    }

    public String getResumen() {
        return mensaje.length() > 100 ? mensaje.substring(0, 100) + "..." : mensaje;
    }
}