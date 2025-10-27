package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
/**
 * Clase que representa el horario de un grupo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "schedules")
public class Schedule {
    @Id
    private String id;

    private String dayOfWeek; // MONDAY, TUESDAY, etc.
    private String startTime;
    private String endTime;
    private String classroom;

    @DBRef
    private Group group;

    private LocalDateTime createdAt;
}