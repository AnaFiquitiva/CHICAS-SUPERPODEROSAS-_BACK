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
 * Clase que representa a un decano en el sistema.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "deans")
public class Dean {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String institutionalEmail;
    private String personalEmail;
    private String phone;

    @DBRef
    private Faculty faculty;

    private boolean active;

    @DBRef
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}