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
 * Clase que representa los roles disponibles en el sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "roles")
public class Role {
    @Id
    private String id;

    private String name; // ADMIN, DEAN, PROFESSOR, STUDENT
    private String description;

    @DBRef
    private List<Permission> permissions;

    private LocalDateTime createdAt;


}
