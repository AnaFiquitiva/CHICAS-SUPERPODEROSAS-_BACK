package eci.edu.dosw.proyecto.model;


/**
 * Clase para identificar el rol del usuario que realiza la acción.
 * Permite simular permisos de ADMIN y DEAN para pruebas.
 * En un entorno real, se debe reemplazar con Spring Security.
 */
public class RoleIdentifier {

    // Simulación del rol actual del usuario (puede ser "ADMIN" o "DEAN")
    private static String currentRole = "ADMIN"; // Por defecto Admin para pruebas

    // Simulación del email/username del usuario autenticado
    private static String currentUserEmail = "admin@example.com";

    /**
     * Verifica si el usuario es ADMIN.
     */
    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(currentRole);
    }

    /**
     * Verifica si el usuario es DEAN.
     */
    public static boolean isDean() {
        return "DEAN".equalsIgnoreCase(currentRole);
    }

    /**
     * Obtiene el email o identificador del usuario actual.
     */
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    /**
     * Cambia el rol actual (para pruebas).
     */
    public static void setCurrentRole(String role) {
        currentRole = role;
    }

    /**
     * Cambia el usuario actual (para pruebas).
     */
    public static void setCurrentUserEmail(String email) {
        currentUserEmail = email;
    }
}
