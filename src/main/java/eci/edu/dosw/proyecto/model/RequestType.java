package eci.edu.dosw.proyecto.model;

/**
 * TIPOS DE SOLICITUD
 */
public enum RequestType {
    GROUP_CHANGE,      // Cambio de grupo (misma materia)
    SUBJECT_CHANGE,    // Cambio de materia (diferente materia)
    PLAN_CHANGE,       // Cambio de materias en el plan de estudio
    NEW_ENROLLMENT     // Nueva inscripción sin materia actual
}