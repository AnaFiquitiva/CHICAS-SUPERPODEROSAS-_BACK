package src.main.java.eci.edu.dosw.proyecto.dto;


import lombok.Data;

@Data
public class MateriaResponseDTO {

    private String nombre;
    private String codigo;
    private String descripcion;
    private String periodoAcademicoId;

    private String status;  // Estado de la operación
    private String mensaje; // Mensaje de éxito o error
}
