package eci.edu.dosw.proyecto.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Clase que representa una materia/cátedra académica.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "subjects")
public class Subject {
    @Id
    private String id;

    private String code;
    private String name;
    private String description;
    private Integer credits;

    @DBRef
    private Faculty faculty;

    @DBRef
    private List<Subject> prerequisites;

    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}