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
public class MateriaConGruposDTO {

    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer creditos;
    private String facultad;
    private String departamento;
    private List<MateriaSummaryDTO> prerequisitos;
    private List<GrupoSummaryDTO> grupos;
}