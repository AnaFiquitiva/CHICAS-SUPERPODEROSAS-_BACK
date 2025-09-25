@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class ProfesorRequestDTO extends UsuarioBaseDTO {

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El departamento es obligatorio")
    private String departamento;

    private String especialidad;
}

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class ProfesorResponseDTO extends UsuarioBaseDTO {

    private String departamento;
    private String especialidad;
    private int numeroGrupos;
}
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfesorSummaryDTO {
    private String id;
    private String codigo;
    private String nombre;
    private String departamento;
    private String especialidad;
}