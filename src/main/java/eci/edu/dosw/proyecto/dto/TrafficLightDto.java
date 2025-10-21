package eci.edu.dosw.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficLightDto {
    private String studentCode;
    private String name;
    private double average;
    private String status; // GREEN, YELLOW, or RED
}
