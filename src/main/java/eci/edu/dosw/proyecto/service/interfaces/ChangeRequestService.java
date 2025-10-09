package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;

import java.util.List;

public interface ChangeRequestService {

    // Métodos de creación y gestión
    ChangeRequestResponseDTO createChangeRequest(String studentId, ChangeRequestRequestDTO requestDTO);
    ChangeRequestResponseDTO cancelRequest(String requestId, String studentId);

    // Métodos de consulta para estudiantes
    List<ChangeRequestResponseDTO> getStudentRequests(String studentId);
    ChangeRequestResponseDTO getStudentRequestById(String studentId, String requestId);
    ChangeRequestResponseDTO getStudentRequestByNumber(String studentId, String requestNumber);
    ChangeRequestResponseDTO getRequestById(String requestId);
    ChangeRequestResponseDTO getRequestByNumber(String requestNumber);

    // NUEVOS MÉTODOS PARA CONSULTA DE ESTADO
    List<ChangeRequestResponseDTO> getStudentRequestsByStatus(String studentId, RequestStatus status);
    List<ChangeRequestResponseDTO> getStudentRequestsByType(String studentId, RequestType type);
    List<ChangeRequestResponseDTO> getActiveRequestsByStudent(String studentId);

    // Métodos de consulta para administradores
    List<ChangeRequestResponseDTO> getAllRequests(RequestStatus status, RequestType type, String studentId, String periodName);
    List<ChangeRequestResponseDTO> getRequestsByStatus(RequestStatus status);
    List<ChangeRequestResponseDTO> getRequestsByType(RequestType type);
    List<ChangeRequestResponseDTO> getRequestsByPeriod(String periodName);
    List<ChangeRequestResponseDTO> getRecentRequests();

    // Métodos de estadísticas
    RequestStatsDTO getRequestStats();
    RequestStatsDTO getDetailedStats(String periodName, RequestType type, String program);
    StudentRequestStatsDTO getStudentRequestStats(String studentId);

    // Métodos de validación
    ValidationResponseDTO validateScheduleConflict(String studentId, String targetGroupId);
    ValidationResponseDTO validateAcademicPeriodActive();
    ValidationResponseDTO validateMaxRequests(String studentId);

    ChangeRequestResponseDTO approveRequest(String requestId, String employeeCode, String comments);
    ChangeRequestResponseDTO rejectRequest(String requestId, String employeeCode, String reason, String comments);
    ChangeRequestResponseDTO requestMoreInformation(String requestId, String employeeCode, String informationNeeded, Integer daysToRespond);
    ChangeRequestResponseDTO provideAdditionalInfo(String requestId, String studentId, String additionalInfo);
    /**
     * Eliminar una solicitud (solo para estudiantes dueños, administradores o decanos)
     */
    public void deleteRequest(String requestId, String userId, String userRole);
}