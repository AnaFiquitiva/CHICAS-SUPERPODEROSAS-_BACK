package eci.edu.dosw.proyecto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EstudianteInfoDTO {
    private String id;
    private String codigo;
    private String nombre;
    private String carrera;
    private Integer semestre;
    private String semaforoAcademico;
}
