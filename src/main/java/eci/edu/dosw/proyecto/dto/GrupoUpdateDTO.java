package eci.edu.dosw.proyecto.dto;

import lombok.Data;

@Data
public class GrupoUpdateDTO {
    private String codigo;
    private String nombre;
    private Integer cupoMaximo;
    private Integer cupoDisponible;
    private Boolean activo;
    private String materiaId;
    private String profesorId;
    private String horarios;
    private String aula;
}