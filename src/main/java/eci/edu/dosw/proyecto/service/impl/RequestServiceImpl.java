package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.RequestRepository;
import eci.edu.dosw.proyecto.repository.UserRepository;
import eci.edu.dosw.proyecto.service.interfaces.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public List<Request> getRequestsByFaculty(String faculty) {
        // Devuelve todas las solicitudes de la facultad del usuario decanatura
        return requestRepository.findByFaculty(faculty);
    }

    @Override
    public List<Request> getRequestsByFacultyAndStatus(String faculty, RequestStatus status) {
        // Devuelve las solicitudes filtradas por estado
        return requestRepository.findByFacultyAndStatus(faculty, status);
    }

    @Override
    public Request respondRequest(String requestId, String action, String comment, String deanFaculty) {
        // Busca la solicitud
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Verifica que la solicitud pertenezca a la misma facultad
        if (!request.getFaculty().equals(deanFaculty)) {
            throw new RuntimeException("You cannot modify requests from another faculty");
        }

        // Procesa la acción: aprobar, rechazar o pedir más info
        switch (action.toUpperCase()) {
            case "APPROVE":
                // Validación ficticia: cupo y horario
                boolean hasAvailableSpace = true;
                boolean hasNoScheduleConflict = true;

                if (!hasAvailableSpace || !hasNoScheduleConflict) {
                    throw new RuntimeException("Cannot approve: no available space or schedule conflict");
                }

                request.setStatus(RequestStatus.APPROVED);
                request.setComment(comment != null ? comment : "Request approved successfully");
                break;

            case "REJECT":
                request.setStatus(RequestStatus.REJECTED);
                request.setComment(comment != null ? comment : "Request rejected by dean");
                break;

            case "REQUEST_INFO":
                request.setStatus(RequestStatus.REQUESTED_INFO);
                request.setComment(comment != null ? comment : "More information required");
                break;

            default:
                throw new RuntimeException("Invalid action. Must be APPROVE, REJECT or REQUEST_INFO.");
        }

        return requestRepository.save(request);
    }

    @Override
    public Request updateRequestWithInfo(String requestId, String studentId, String comment) {
        // Busca la solicitud
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Verifica que el estudiante sea el dueño de la solicitud
        if (!request.getStudent().getId().equals(studentId)) {
            throw new RuntimeException("You are not authorized to modify this request");
        }

        // Solo se puede actualizar si está en estado REQUESTED_INFO
        if (request.getStatus() != RequestStatus.REQUESTED_INFO) {
            throw new RuntimeException("You cannot update a request that is not awaiting information");
        }

        // Actualiza el comentario con la información adicional y cambia a estado UNDER_REVIEW
        request.setComment(comment);
        request.setStatus(RequestStatus.UNDER_REVIEW);

        return requestRepository.save(request);
    }
}
