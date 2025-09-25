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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MateriaRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^[A-Z0-9]{4,10}$", message = "El código debe tener entre 4-10 caracteres alfanuméricos")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "Los créditos son obligatorios")
    @Min(value = 1, message = "Los créditos deben ser mayor a 0")
    @Max(value = 6, message = "Los créditos no pueden exceder 6")
    private Integer creditos;

    @NotBlank(message = "La facultad es obligatoria")
    private String facultad;

    @NotBlank(message = "El departamento es obligatorio")
    private String departamento;

    @Min(value = 1, message = "El semestre recomendado debe ser mayor a 0")
    @Max(value = 12, message = "El semestre recomendado no puede exceder 12")
    private Integer semestreRecomendado;

    private boolean electiva = false;
    private boolean activa = true;

    private List<String> prerequisitosIds;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MateriaResponseDTO {

    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer creditos;
    private String facultad;
    private String departamento;
    private Integer semestreRecomendado;
    private boolean electiva;
    private boolean activa;
    private int numeroGruposActivos;
    private boolean tieneCuposDisponibles;
}


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MateriaSummaryDTO {

    private String id;
    private String codigo;
    private String nombre;
    private Integer creditos;
    private String facultad;
    private boolean electiva;
    private int numeroGruposActivos;
}