package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
/**
 * Clase que representa una facultad en la universidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "faculties")
public class Faculty {
    @Id
    private String id;

    private String code;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}