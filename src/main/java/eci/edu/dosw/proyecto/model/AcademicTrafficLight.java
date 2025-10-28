package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
/**
 * Clase que representa el semáforo académico de un estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "academic_traffic_lights")
public class AcademicTrafficLight {
    @Id
    private String id;

    private String color; // GREEN, YELLOW, RED
    private String description;
    private Double minimumGpa;
    private Double maximumGpa;
    private LocalDateTime calculatedAt;
}