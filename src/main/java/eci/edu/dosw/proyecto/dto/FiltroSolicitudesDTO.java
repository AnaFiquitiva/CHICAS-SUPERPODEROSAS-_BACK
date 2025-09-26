package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FiltroSolicitudesDTO {
    private String estado;
    private String facultad;
    private String estudianteId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}