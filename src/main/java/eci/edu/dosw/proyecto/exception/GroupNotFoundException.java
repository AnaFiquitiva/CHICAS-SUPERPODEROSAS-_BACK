package eci.edu.dosw.proyecto.exception;


import eci.edu.dosw.proyecto.dto.GroupAssignmentRequest;
import eci.edu.dosw.proyecto.dto.GroupAssignmentResponse;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.EnrollmentRepository;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.repository.WaitingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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


