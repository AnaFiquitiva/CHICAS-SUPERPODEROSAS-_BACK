package eci.edu.dosw.proyecto.model;

/**
 * Utilidades para validar roles
 */
public final class RolValidator {

    public static boolean esRolValido(String rol) {
        return Roles.ESTUDIANTE.equals(rol) ||
                Roles.DECANATURA.equals(rol) ||
                Roles.ADMINISTRADOR.equals(rol);
    }

    public static boolean puedeGestionarSolicitudes(String rol) {
        return Roles.DECANATURA.equals(rol) ||
                Roles.ADMINISTRADOR.equals(rol);
    }

    public static boolean puedeCrearSolicitudes(String rol) {
        return Roles.ESTUDIANTE.equals(rol);
    }

    private RolValidator() {} // Utility class
}
