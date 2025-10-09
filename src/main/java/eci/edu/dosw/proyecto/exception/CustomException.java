package eci.edu.dosw.proyecto.exception;


/**
 * Excepción personalizada utilizada para manejar errores específicos de negocio.
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
