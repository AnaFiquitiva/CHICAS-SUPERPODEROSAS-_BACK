package eci.edu.dosw.proyecto.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import eci.edu.dosw.proyecto.model.*;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;



@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class DecanoRequestDTO extends UsuarioBaseDTO {

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "La facultad es obligatoria")
    private String facultad;

    private String departamento;

    @Pattern(regexp = "^[0-9+\\-\\s\\(\\)]+$", message = "Formato de teléfono inválido")
    private String telefonoOficina;
}

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
class DecanoResponseDTO extends UsuarioBaseDTO {

    private String facultad;
    private String departamento;
    private String telefonoOficina;
    private int numeroMateriasGestionadas;
    private long solicitudesPendientes;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecanoSummaryDTO {
    private String id;
    private String codigo;
    private String nombre;
    private String facultad;
    private String departamento;
}