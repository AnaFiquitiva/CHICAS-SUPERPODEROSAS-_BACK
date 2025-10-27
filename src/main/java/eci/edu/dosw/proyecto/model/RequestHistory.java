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
 * Clase que representa el historial de cambios de una solicitud.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "request_histories")
public class RequestHistory {
    @Id
    private String id;

    @DBRef
    private Request request;

    private RequestStatus previousStatus;
    private RequestStatus newStatus;
    private String comments;

    @DBRef
    private User changedBy;

    private LocalDateTime changedAt;
}
