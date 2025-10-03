package eci.edu.dosw.proyecto.dto;


import lombok.Data;

@Data
public class GrupoResponseDTO {

    private String codigo;
    private String materiaId;
    private String profesorId;

    private String status;  // Estado de la operación
    private String mensaje; // Mensaje de éxito o error
}
