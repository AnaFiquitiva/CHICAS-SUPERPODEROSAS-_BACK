package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Clase que representa la configuración de períodos académicos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "academic_periods")
public class AcademicPeriod {
    @Id
    private String id;

    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
    private LocalDateTime createdAt;
}