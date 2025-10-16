package eci.edu.dosw.proyecto.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    private String id;

    // Estudiante que hace la solicitud
    private User student;

    // Materia actual que el estudiante quiere cambiar
    private String currentSubject;

    // Materia o grupo al que el estudiante quiere moverse
    private String targetSubject;

    // Estado actual de la solicitud (pendiente, aprobada, etc.)
    private RequestStatus status;

    // Comentario o mensaje de la decanatura o del estudiante
    private String comment;

    // Fecha de creación (opcional)
    private String createdAt;

    // Facultad a la que pertenece la solicitud (útil para filtrar por decanatura)
    private String faculty;

}
