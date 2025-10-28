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
 * Historial de cambios en la capacidad de grupos.
 * Funcionalidad: 30 (Modificar cupos)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "group_capacity_history")
public class GroupCapacityHistory {
    @Id
    private String id;

    @DBRef
    private Group group;

    private Integer previousCapacity;
    private Integer newCapacity;
    private String changeReason;
    private String justification;

    @DBRef
    private User modifiedBy;

    private LocalDateTime modifiedAt;

    // Método para verificar si la modificación es válida
    public boolean isValidCapacityChange() {
        return newCapacity != null &&
                newCapacity > 0 &&
                (previousCapacity == null || newCapacity >= group.getCurrentEnrollment());
    }
}