package eci.edu.dosw.proyecto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que representa la lista de espera de un grupo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "waiting_lists")
public class WaitingList {
    @Id
    private String id;

    @DBRef
    private Group group;

    @DBRef
    private List<WaitingListEntry> entries;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}