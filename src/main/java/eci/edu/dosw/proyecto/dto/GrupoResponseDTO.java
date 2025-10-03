package src.main.java.eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GrupoResponseDTO {

    private String nombre;
    private String horarioId;
    private String materiaId;
    private String periodoAcademicoId;

    private String status;  // Estado de la operación
    private String mensaje; // Mensaje de éxito o error
}
