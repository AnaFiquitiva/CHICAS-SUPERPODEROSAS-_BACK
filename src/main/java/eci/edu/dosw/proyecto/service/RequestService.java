package eci.edu.dosw.proyecto.service;

import eci.edu.dosw.proyecto.model.Request;
import eci.edu.dosw.proyecto.model.RequestStatus;
import java.util.List;

public interface RequestService {

    // Obtener todas las solicitudes de una facultad
    List<Request> getRequestsByFaculty(String faculty);

    // Filtrar solicitudes por estado dentro de una facultad
    List<Request> getRequestsByFacultyAndStatus(String faculty, RequestStatus status);

    // Aprobar o rechazar solicitud (solo decanatura)
    Request respondRequest(String requestId, String action, String comment, String deanFaculty);

    // Actualizar información adicional (solo estudiante)
    Request updateRequestWithInfo(String requestId, String studentId, String comment);
}
