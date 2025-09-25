@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class AdministradorRequestDTO extends UsuarioBaseDTO {

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nivel de acceso es obligatorio")
    private String nivelAcceso;

    private boolean puedeConfigurarSistema = true;
    private boolean puedeGestionarUsuarios = true;
}

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class AdministradorResponseDTO extends UsuarioBaseDTO {

    private String nivelAcceso;
    private boolean puedeConfigurarSistema;
    private boolean puedeGestionarUsuarios;
    private boolean tieneAccesoTotal;
}
