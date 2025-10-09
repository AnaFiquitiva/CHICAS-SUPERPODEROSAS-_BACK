package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import java.util.List;

public interface ExceptionalCaseService {

    // Para estudiantes
    ExceptionalCaseResponseDTO createCase(String studentId, ExceptionalCaseRequestDTO requestDTO);
    List<ExceptionalCaseResponseDTO> getStudentCases(String studentId);
    ExceptionalCaseResponseDTO getStudentCaseById(String studentId, String caseId);
    ExceptionalCaseResponseDTO cancelCase(String caseId, String studentId);
    ExceptionalCaseResponseDTO provideAdditionalInfo(String caseId, String studentId, String information);

    // Para administradores/decanos
    List<ExceptionalCaseResponseDTO> getAllCases(String employeeCode);
    List<ExceptionalCaseResponseDTO> getCasesByStatus(String employeeCode, String status);
    List<ExceptionalCaseResponseDTO> getAssignedCases(String employeeCode);
    ExceptionalCaseResponseDTO getCaseById(String caseId, String employeeCode);
    ExceptionalCaseResponseDTO assignCase(String caseId, String assignedBy, String assignTo);
    ExceptionalCaseResponseDTO requestMoreInfo(String caseId, String employeeCode, String informationNeeded, Integer daysToRespond);
    ExceptionalCaseResponseDTO resolveCase(String caseId, ExceptionalCaseResolutionDTO resolutionDTO);

    // Consultas y estadísticas
    List<ExceptionalCaseResponseDTO> getPendingCases(String employeeCode);
    List<ExceptionalCaseResponseDTO> getUrgentCases(String employeeCode);
    ExceptionalCaseStatsDTO getCaseStats(String employeeCode);
}