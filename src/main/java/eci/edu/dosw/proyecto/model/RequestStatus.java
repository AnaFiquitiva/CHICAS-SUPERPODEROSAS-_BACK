package eci.edu.dosw.proyecto.model;

/**
 * Estado de la solicitud
 */
public enum RequestStatus {
    UNDER_REVIEW,
    APPROVED,
    NEEDS_INFO,
    CANCELLED,
    COMPLETED,
    PENDING,           // Solicitud enviada y en espera
    REJECTED,          // Solicitud rechazada
    REQUESTED_INFO;    // Se pidió información adicional al solicitante
}
