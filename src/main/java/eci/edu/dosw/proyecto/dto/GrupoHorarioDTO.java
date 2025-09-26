package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class GrupoHorarioDTO {
    private String materiaNombre;
    private String grupoCodigo;
    private String horario;
    private String profesor;
    private String aula;
}