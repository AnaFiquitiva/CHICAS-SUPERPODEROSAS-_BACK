package eci.edu.dosw.proyecto.model;

import lombok.Data;

@Data
/*
 * Detalle de cambios en el plan de estudios para PLAN_CHANGE
 */
public class PlanChangeDetail {
    private String action;              // ADD, REMOVE, REPLACE
    private String subjectId;           // Materia afectada
    private String groupId;             // Grupo afectado (para ADD/REPLACE)
    private String replacementSubjectId; // Materia de reemplazo (para REPLACE)
    private String replacementGroupId;   // Grupo de reemplazo (para REPLACE)
    private String reason;              // Justificación del cambio


}