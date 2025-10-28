package eci.edu.dosw.proyecto.model;

/**
 * Enumeración para estados de asignación manual
 */
public enum AssignmentStatus {
    PENDING,     // Pendiente de ejecución
    EXECUTED,    // Ejecutada exitosamente
    FAILED,      // Falló en la ejecución
    CANCELLED,   // Cancelada por el usuario
    VALIDATED    // Validada, lista para ejecución
}
