package eci.edu.dosw.proyecto.model;

import lombok.Data;

/**
 * Clase que representa el horario de un grupo
 */
@Data
public class Schedule {
    private String dayOfWeek; // LUNES, MARTES, etc.
    private String startTime;
    private String endTime;
    private String classroom;
}