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
 * Clase que representa un programa académico.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "programs")
public class Program {
    @Id
    private String id;

    private String code;
    private String name;
    private String description;

    @DBRef
    private Faculty faculty;

    private Integer totalCredits;
    private Integer durationSemesters;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
