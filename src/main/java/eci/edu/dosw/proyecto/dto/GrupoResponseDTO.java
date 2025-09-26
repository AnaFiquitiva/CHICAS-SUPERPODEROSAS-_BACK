package eci.edu.dosw.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrupoResponseDTO {

    private String id;
    private String codigo;
    private Integer cupoMaximo;
    private Integer capacidadActual;
    private Integer disponibilidad;
    private boolean activo;
    private String aula;
    private String edificio;
    private double porcentajeOcupacion;
    private boolean estaCercaDelLimite;
    private boolean estaLleno;

    private ProfesorSummaryDTO profesor;
    private MateriaSummaryDTO materia;
}
