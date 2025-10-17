package eci.edu.dosw.proyecto.exception;

/**
 * Custom exception for student not found scenarios
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
