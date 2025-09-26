package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GrupoInfoDTO {
    private String id;
    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private String profesor;
    private Boolean tieneCupo;
}