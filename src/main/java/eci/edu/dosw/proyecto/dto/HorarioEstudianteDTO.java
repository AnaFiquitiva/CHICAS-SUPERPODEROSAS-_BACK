package eci.edu.dosw.proyecto.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class HorarioEstudianteDTO {
    private String estudianteId;
    private String estudianteNombre;
    private String semestre;
    private List<GrupoHorarioDTO> horarios;
}
