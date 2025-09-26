package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class DashboardDTO {
    private Long totalEstudiantes;
    private Long totalSolicitudes;
    private Long solicitudesPendientes;
    private Long solicitudesAprobadas;
    private Long solicitudesRechazadas;
    private Map<String, Long> solicitudesPorFacultad;
    private Map<String, Long> estudiantesPorCarrera;
    private Map<String, Long> gruposCercaDelLimite;
}