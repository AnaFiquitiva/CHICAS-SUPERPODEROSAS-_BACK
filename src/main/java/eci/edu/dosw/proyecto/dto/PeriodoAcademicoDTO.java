package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PeriodoAcademicoDTO {
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaInicioSolicitudes;
    private LocalDateTime fechaFinSolicitudes;
    private String descripcion;
}
