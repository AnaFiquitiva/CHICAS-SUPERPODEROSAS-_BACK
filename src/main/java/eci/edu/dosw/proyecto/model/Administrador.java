package eci.edu.dosw.proyecto.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "administradores")
public class Administrador extends Usuario {

    private String nivelAcceso;
    private boolean puedeConfigurarSistema;
    private boolean puedeGestionarUsuarios;

    public Administrador() {
        super();
        this.setRol(RolUsuario.ADMINISTRADOR);
        this.puedeConfigurarSistema = true;
        this.puedeGestionarUsuarios = true;
    }

    public boolean tieneAccesoTotal() {
        return "TOTAL".equals(nivelAcceso);
    }
}