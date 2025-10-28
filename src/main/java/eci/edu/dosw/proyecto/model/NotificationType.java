package eci.edu.dosw.proyecto.model;

/**
 * Enumeración para tipos de notificación
 */
public enum NotificationType {
    REQUEST_STATUS_CHANGE,   // Cambio en estado de solicitud
    ALERT_TRIGGERED,         // Alerta del sistema (Func. 26)
    INFO_MESSAGE,            // Mensaje informativo
    WARNING_MESSAGE,         // Mensaje de advertencia
    SUCCESS_MESSAGE,         // Mensaje de éxito
    DEADLINE_REMINDER,       // Recordatorio de fecha límite
    SPECIAL_APPROVAL         // Aprobación especial (Func. 23)
}
