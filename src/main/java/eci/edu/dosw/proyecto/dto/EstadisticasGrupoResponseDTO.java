package src.main.java.eci.edu.dosw.proyecto.dto;


import lombok.Data;

@Data
public class EstadisticasGrupoResponseDTO {
    private String grupoId;
    private int totalEstudiantes;
    private int estudiantesAprobados;
    private int estudiantesReprobados;

    private String status;  // Estado de la operación
    private String mensaje; // Mensaje de éxito o error
}
