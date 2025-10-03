package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class DashboardResponseDTO {
    private String periodoAcademicoId;
    private int numeroEstudiantes;
    private int numeroGrupos;
    private int numeroMaterias;

    private String status;  // Estado de la operación
    private String mensaje; // Mensaje de éxito o error
}
