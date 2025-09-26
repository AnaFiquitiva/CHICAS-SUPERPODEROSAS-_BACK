package eci.edu.dosw.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoSummaryDTO {

    private String id;
    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private String aula;
    private String edificio;
    private String materiaCodigoNombre;
    private String profesorNombre;
    private boolean tieneCupo;
}
