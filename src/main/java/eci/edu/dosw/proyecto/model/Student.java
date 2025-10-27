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
 * Clase que representa a un estudiante en el sistema.
 * Contiene información personal y académica del estudiante.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "students")
public class Student {
    @Id
    private String id;

    private String code;
    private String firstName;
    private String lastName;
    private String institutionalEmail;
    private String personalEmail;
    private String phone;
    private String address;

    @DBRef
    private Program program;

    private Integer currentSemester;

    @DBRef
    private AcademicTrafficLight trafficLight;

    private boolean active;

    @DBRef
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}