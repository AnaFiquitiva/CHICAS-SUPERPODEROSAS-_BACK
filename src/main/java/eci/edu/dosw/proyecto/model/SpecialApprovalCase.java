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
 * Casos excepcionales aprobados manualmente.
 * Funcionalidades: 23 (Aprobación especial), 24 (Consultar casos excepcionales)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "special_approval_cases")
public class SpecialApprovalCase {
    @Id
    private String id;

    @DBRef
    private Request request;

    @DBRef
    private User approvedBy;

    private String justification;
    private String constraintsOverridden; // CUPO, PREREQUISITOS, HORARIO, etc.

    private LocalDateTime approvedAt;

    // Método para obtener descripción de restricciones sobrepasadas
    public String getOverriddenConstraintsDescription() {
        return "Aprobación especial que sobrepasó: " + constraintsOverridden;
    }
}

