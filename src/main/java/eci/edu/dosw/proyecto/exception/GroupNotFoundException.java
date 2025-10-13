package eci.edu.dosw.proyecto.exception;


/**
 * Excepción que indica que no se encontró un grupo.
 * Extiende la NotFoundException ya presente en el proyecto para que
 * sea manejada por el GlobalExceptionHandler existente.
 */
public class GroupNotFoundException extends NotFoundException {

    public GroupNotFoundException(String message) {
        super(message);
    }

    public GroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
