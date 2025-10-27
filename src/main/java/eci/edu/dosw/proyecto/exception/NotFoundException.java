package eci.edu.dosw.proyecto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

        public NotFoundException(String entity, String id) {
            super(entity + " con ID " + id + " no encontrado");
        }
        public NotFoundException(String message) {
            super(message);
        }


}