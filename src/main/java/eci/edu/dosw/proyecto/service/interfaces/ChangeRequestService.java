package eci.edu.dosw.proyecto.service.interfaces;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.dto.*;

import java.util.List;

/**
 * Servicio para la gestión de solicitudes de cambio.
 */
public interface ChangeRequestService {
    ChangeRequestResponseDTO createChangeRequest(ChangeRequestDTO changeRequestDTO);
    ChangeRequestResponseDTO getChangeRequestById(String id);
    List<ChangeRequestResponseDTO> getChangeRequestsByStudent(String studentId);
    List<ChangeRequestResponseDTO> getChangeRequestsByFaculty(String faculty);
    List<ChangeRequestResponseDTO> getChangeRequestsByStatus(ChangeRequest.RequestStatus status);
    ChangeRequestResponseDTO updateChangeRequestStatus(String id, ChangeRequest.RequestStatus status);
    // Otros métodos que puedan ser necesarios
}