package eci.edu.dosw.proyecto.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO for academic recommendations
 * Provides actionable advice based on student's academic status
 */
@Data
public class Recommendations {
    private List<String> academic;
    private List<String> administrative;
    private List<String> support;
}
