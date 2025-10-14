package eci.edu.dosw.proyecto.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Representa a una estudiante en la lista de espera de un grupo.
 */
@Data
@Document(collection = "waiting_list")
public class WaitingListEntry {

    @Id
    private String id;
    private String groupId;
    private String studentId;
    private LocalDateTime requestDate;
}
