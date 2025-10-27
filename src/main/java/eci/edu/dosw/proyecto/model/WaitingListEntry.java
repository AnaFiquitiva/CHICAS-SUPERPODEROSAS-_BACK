package eci.edu.dosw.proyecto.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
/**
 * Clase que representa una entrada en la lista de espera.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "waiting_list_entries")
public class WaitingListEntry {
    @Id
    private String id;

    @DBRef
    private Student student;

    @DBRef
    private WaitingList waitingList;

    private Integer position;
    private LocalDateTime joinedAt;
    private boolean active;
}