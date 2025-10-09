package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.ExceptionalCaseRepository;
import eci.edu.dosw.proyecto.repository.StudentRepository;
import eci.edu.dosw.proyecto.service.interfaces.ExceptionalCaseService;
import eci.edu.dosw.proyecto.service.interfaces.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExceptionalCaseServiceImpl implements ExceptionalCaseService {

    private final ExceptionalCaseRepository exceptionalCaseRepository;
    private final StudentRepository studentRepository;
    private final PermissionService permissionService;

    @Override
    @Transactional
    public ExceptionalCaseResponseDTO createCase(String studentId, ExceptionalCaseRequestDTO requestDTO) {
        // Validar que el estudiante existe
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        // Validar límite de casos activos (reutilizando lógica similar)
        validateActiveCasesLimit(studentId);

        // Crear el caso
        ExceptionalCase exceptionalCase = new ExceptionalCase();
        exceptionalCase.setCaseNumber(generateCaseNumber());
        exceptionalCase.setStudentId(studentId);
        exceptionalCase.setStudentName(student.getName());
        exceptionalCase.setStudentProgram(student.getProgram());
        exceptionalCase.setStudentSemester(student.getCurrentSemester());

        exceptionalCase.setTitle(requestDTO.getTitle());
        exceptionalCase.setDescription(requestDTO.getDescription());
        exceptionalCase.setCaseType(requestDTO.getCaseType());
        exceptionalCase.setJustification(requestDTO.getJustification());
        exceptionalCase.setSupportingDocuments(requestDTO.getSupportingDocuments());
        exceptionalCase.setPriority(requestDTO.getPriority() != null ? requestDTO.getPriority() : 2);

        // Usando RequestStatus existente
        exceptionalCase.setStatus(RequestStatus.PENDING);
        exceptionalCase.setCreationDate(LocalDateTime.now());
        exceptionalCase.setResponseDeadline(LocalDateTime.now().plusDays(10));

        // Historial inicial (similar a ChangeRequest)
        ExceptionalCaseHistory creationHistory = new ExceptionalCaseHistory();
        creationHistory.setTimestamp(LocalDateTime.now());
        creationHistory.setAction("CREATED");
        creationHistory.setDescription("Caso excepcional creado por el estudiante");
        creationHistory.setUserId(studentId);
        creationHistory.setUserRole("STUDENT");
        creationHistory.setComments("Caso creado: " + requestDTO.getTitle());

        exceptionalCase.setHistory(List.of(creationHistory));

        ExceptionalCase savedCase = exceptionalCaseRepository.save(exceptionalCase);
        log.info("Caso excepcional creado: {} para estudiante {}", savedCase.getCaseNumber(), studentId);

        return mapToDTO(savedCase);
    }

    @Override
    public List<ExceptionalCaseResponseDTO> getStudentCases(String studentId) {
        List<ExceptionalCase> cases = exceptionalCaseRepository.findByStudentId(studentId);
        return cases.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public ExceptionalCaseResponseDTO getStudentCaseById(String studentId, String caseId) {
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        if (!exceptionalCase.getStudentId().equals(studentId)) {
            throw new BusinessException("No tienes permiso para ver este caso");
        }

        return mapToDTO(exceptionalCase);
    }

    @Override
    @Transactional
    public ExceptionalCaseResponseDTO cancelCase(String caseId, String studentId) {
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        if (!exceptionalCase.getStudentId().equals(studentId)) {
            throw new BusinessException("No puedes cancelar un caso que no te pertenece");
        }

        // Validación de estado similar a ChangeRequest
        if (exceptionalCase.getStatus() != RequestStatus.PENDING &&
                exceptionalCase.getStatus() != RequestStatus.NEEDS_INFO) {
            throw new BusinessException("Solo se pueden cancelar casos pendientes o que necesitan información");
        }

        exceptionalCase.setStatus(RequestStatus.CANCELLED);

        // Agregar al historial (similar a ChangeRequest)
        ExceptionalCaseHistory cancelHistory = new ExceptionalCaseHistory();
        cancelHistory.setTimestamp(LocalDateTime.now());
        cancelHistory.setAction("CANCELLED");
        cancelHistory.setDescription("Caso cancelado por el estudiante");
        cancelHistory.setUserId(studentId);
        cancelHistory.setUserRole("STUDENT");

        addToHistory(exceptionalCase, cancelHistory);

        ExceptionalCase updatedCase = exceptionalCaseRepository.save(exceptionalCase);
        return mapToDTO(updatedCase);
    }

    @Override
    @Transactional
    public ExceptionalCaseResponseDTO resolveCase(String caseId, ExceptionalCaseResolutionDTO resolutionDTO) {
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        if (!permissionService.canViewAllRequests(resolutionDTO.getEmployeeCode())) {
            throw new BusinessException("No tiene permisos para resolver casos excepcionales");
        }

        if (exceptionalCase.getStatus() == RequestStatus.APPROVED ||
                exceptionalCase.getStatus() == RequestStatus.REJECTED) {
            throw new BusinessException("El caso ya ha sido resuelto");
        }

        if ("APPROVED".equals(resolutionDTO.getResolution())) {
            exceptionalCase.setStatus(RequestStatus.APPROVED);
        } else if ("REJECTED".equals(resolutionDTO.getResolution())) {
            exceptionalCase.setStatus(RequestStatus.REJECTED);
        } else {
            throw new BusinessException("Resolución no válida. Use APPROVED o REJECTED");
        }

        exceptionalCase.setResolution(resolutionDTO.getResolution());
        exceptionalCase.setResolutionComments(resolutionDTO.getComments());
        exceptionalCase.setResolvedBy(resolutionDTO.getEmployeeCode());
        exceptionalCase.setResolutionDate(LocalDateTime.now());

        // Agregar al historial (similar a ChangeRequest)
        ExceptionalCaseHistory resolutionHistory = new ExceptionalCaseHistory();
        resolutionHistory.setTimestamp(LocalDateTime.now());
        resolutionHistory.setAction(resolutionDTO.getResolution());
        resolutionHistory.setDescription("Caso " + resolutionDTO.getResolution().toLowerCase() + " por " + resolutionDTO.getEmployeeCode());
        resolutionHistory.setUserId(resolutionDTO.getEmployeeCode());
        resolutionHistory.setUserRole("ADMIN/DEAN");
        resolutionHistory.setComments(resolutionDTO.getComments());

        addToHistory(exceptionalCase, resolutionHistory);

        ExceptionalCase resolvedCase = exceptionalCaseRepository.save(exceptionalCase);
        log.info("Caso {} resuelto como {} por {}", caseId, resolutionDTO.getResolution(), resolutionDTO.getEmployeeCode());

        return mapToDTO(resolvedCase);
    }

    // Métodos auxiliares reutilizados de la lógica existente
    private void validateActiveCasesLimit(String studentId) {
        Long activeCases = exceptionalCaseRepository.countActiveCasesByStudent(studentId);
        if (activeCases >= 3) { // Límite de 3 casos activos por estudiante
            throw new BusinessException("Has alcanzado el límite de casos excepcionales activos. " +
                    "Espera a que se resuelvan los casos actuales antes de crear uno nuevo.");
        }
    }

    private String generateCaseNumber() {
        return "CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void addToHistory(ExceptionalCase exceptionalCase, ExceptionalCaseHistory history) {
        if (exceptionalCase.getHistory() == null) {
            exceptionalCase.setHistory(new ArrayList<>());
        }
        exceptionalCase.getHistory().add(history);
    }

    private ExceptionalCaseResponseDTO mapToDTO(ExceptionalCase exceptionalCase) {
        ExceptionalCaseResponseDTO dto = new ExceptionalCaseResponseDTO();
        dto.setId(exceptionalCase.getId());
        dto.setCaseNumber(exceptionalCase.getCaseNumber());
        dto.setStudentId(exceptionalCase.getStudentId());
        dto.setStudentName(exceptionalCase.getStudentName());
        dto.setStudentProgram(exceptionalCase.getStudentProgram());
        dto.setStudentSemester(exceptionalCase.getStudentSemester());
        dto.setTitle(exceptionalCase.getTitle());
        dto.setDescription(exceptionalCase.getDescription());
        dto.setCaseType(exceptionalCase.getCaseType());
        dto.setJustification(exceptionalCase.getJustification());
        dto.setSupportingDocuments(exceptionalCase.getSupportingDocuments());
        dto.setStatus(exceptionalCase.getStatus().toString());
        dto.setPriority(exceptionalCase.getPriority());
        dto.setAssignedTo(exceptionalCase.getAssignedTo());
        dto.setCreationDate(exceptionalCase.getCreationDate());
        dto.setReviewDate(exceptionalCase.getReviewDate());
        dto.setResolutionDate(exceptionalCase.getResolutionDate());
        dto.setResolution(exceptionalCase.getResolution());
        dto.setResolutionComments(exceptionalCase.getResolutionComments());
        dto.setResolvedBy(exceptionalCase.getResolvedBy());
        dto.setResponseDeadline(exceptionalCase.getResponseDeadline());

        if (exceptionalCase.getHistory() != null) {
            dto.setHistory(exceptionalCase.getHistory().stream().map(this::mapHistoryToDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    private ExceptionalCaseHistoryDTO mapHistoryToDTO(ExceptionalCaseHistory history) {
        ExceptionalCaseHistoryDTO dto = new ExceptionalCaseHistoryDTO();
        dto.setTimestamp(history.getTimestamp());
        dto.setAction(history.getAction());
        dto.setDescription(history.getDescription());
        dto.setUserId(history.getUserId());
        dto.setUserRole(history.getUserRole());
        dto.setComments(history.getComments());
        return dto;
    }

    @Override
    public List<ExceptionalCaseResponseDTO> getAllCases(String employeeCode) {
        validateAdminAccess(employeeCode);
        return exceptionalCaseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExceptionalCaseResponseDTO> getCasesByStatus(String employeeCode, String status) {
        validateAdminAccess(employeeCode);
        RequestStatus requestStatus = RequestStatus.valueOf(status);
        return exceptionalCaseRepository.findByStatus(requestStatus).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExceptionalCaseResponseDTO> getPendingCases(String employeeCode) {
        validateAdminAccess(employeeCode);
        return exceptionalCaseRepository.findPendingCases().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void validateAdminAccess(String employeeCode) {
        if (!permissionService.canViewAllRequests(employeeCode)) {
            throw new BusinessException("No tiene permisos para acceder a casos excepcionales");
        }
    }

    @Override
    public ExceptionalCaseResponseDTO provideAdditionalInfo(String caseId, String studentId, String information) {
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        if (!exceptionalCase.getStudentId().equals(studentId)) {
            throw new BusinessException("No tienes permiso para modificar este caso");
        }

        if (exceptionalCase.getStatus() != RequestStatus.NEEDS_INFO) {
            throw new BusinessException("Este caso no requiere información adicional");
        }

        exceptionalCase.setStatus(RequestStatus.UNDER_REVIEW);

        ExceptionalCaseHistory infoHistory = new ExceptionalCaseHistory();
        infoHistory.setTimestamp(LocalDateTime.now());
        infoHistory.setAction("INFO_PROVIDED");
        infoHistory.setDescription("Información adicional proporcionada por el estudiante");
        infoHistory.setUserId(studentId);
        infoHistory.setUserRole("STUDENT");
        infoHistory.setComments("Información proporcionada: " + information);

        addToHistory(exceptionalCase, infoHistory);

        return mapToDTO(exceptionalCaseRepository.save(exceptionalCase));
    }

    @Override
    public List<ExceptionalCaseResponseDTO> getAssignedCases(String employeeCode) {
        validateAdminAccess(employeeCode);
        return exceptionalCaseRepository.findByAssignedTo(employeeCode).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExceptionalCaseResponseDTO getCaseById(String caseId, String employeeCode) {
        validateAdminAccess(employeeCode);
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));
        return mapToDTO(exceptionalCase);
    }

    @Override
    public ExceptionalCaseResponseDTO assignCase(String caseId, String assignedBy, String assignTo) {
        validateAdminAccess(assignedBy);
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        exceptionalCase.setAssignedTo(assignTo);
        exceptionalCase.setStatus(RequestStatus.UNDER_REVIEW);
        exceptionalCase.setReviewDate(LocalDateTime.now());

        ExceptionalCaseHistory assignHistory = new ExceptionalCaseHistory();
        assignHistory.setTimestamp(LocalDateTime.now());
        assignHistory.setAction("ASSIGNED");
        assignHistory.setDescription("Caso asignado a " + assignTo);
        assignHistory.setUserId(assignedBy);
        assignHistory.setUserRole("ADMIN/DEAN");

        addToHistory(exceptionalCase, assignHistory);

        return mapToDTO(exceptionalCaseRepository.save(exceptionalCase));
    }

    @Override
    public ExceptionalCaseResponseDTO requestMoreInfo(String caseId, String employeeCode, String informationNeeded, Integer daysToRespond) {
        validateAdminAccess(employeeCode);
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findById(caseId)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        exceptionalCase.setStatus(RequestStatus.NEEDS_INFO);
        exceptionalCase.setResponseDeadline(LocalDateTime.now().plusDays(daysToRespond != null ? daysToRespond : 5));

        ExceptionalCaseHistory infoHistory = new ExceptionalCaseHistory();
        infoHistory.setTimestamp(LocalDateTime.now());
        infoHistory.setAction("INFO_REQUESTED");
        infoHistory.setDescription("Información adicional solicitada");
        infoHistory.setUserId(employeeCode);
        infoHistory.setUserRole("ADMIN/DEAN");
        infoHistory.setComments("Información requerida: " + informationNeeded);

        addToHistory(exceptionalCase, infoHistory);

        return mapToDTO(exceptionalCaseRepository.save(exceptionalCase));
    }

    @Override
    public List<ExceptionalCaseResponseDTO> getUrgentCases(String employeeCode) {
        validateAdminAccess(employeeCode);
        return exceptionalCaseRepository.findByPriority(4).stream() // Prioridad 4 = Urgente
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExceptionalCaseStatsDTO getCaseStats(String employeeCode) {
        validateAdminAccess(employeeCode);
        ExceptionalCaseStatsDTO stats = new ExceptionalCaseStatsDTO();

        List<ExceptionalCase> allCases = exceptionalCaseRepository.findAll();

        stats.setTotalCases((long) allCases.size());
        stats.setPendingCases(allCases.stream().filter(c -> c.getStatus() == RequestStatus.PENDING).count());
        stats.setUnderReviewCases(allCases.stream().filter(c -> c.getStatus() == RequestStatus.UNDER_REVIEW).count());
        stats.setApprovedCases(allCases.stream().filter(c -> c.getStatus() == RequestStatus.APPROVED).count());
        stats.setRejectedCases(allCases.stream().filter(c -> c.getStatus() == RequestStatus.REJECTED).count());
        stats.setNeedsInfoCases(allCases.stream().filter(c -> c.getStatus() == RequestStatus.NEEDS_INFO).count());

        // Casos vencidos
        stats.setOverdueCases((long) exceptionalCaseRepository.findOverdueCases(LocalDateTime.now()).size());

        // Estadísticas por tipo
        stats.setAcademicCases(allCases.stream().filter(c -> "ACADEMIC_DIFFICULTY".equals(c.getCaseType())).count());
        stats.setMedicalCases(allCases.stream().filter(c -> "MEDICAL_CONDITION".equals(c.getCaseType())).count());
        stats.setPersonalCases(allCases.stream().filter(c -> "PERSONAL_ISSUES".equals(c.getCaseType())).count());
        stats.setOtherCases(allCases.stream().filter(c -> "OTHER".equals(c.getCaseType())).count());

        // Tasa de aprobación
        long totalResolved = stats.getApprovedCases() + stats.getRejectedCases();
        if (totalResolved > 0) {
            stats.setApprovalRate(Math.round((double) stats.getApprovedCases() / totalResolved * 100 * 100.0) / 100.0);
        }

        return stats;
    }
    /**
     * Obtener casos activos por estudiante (PENDIENTE, EN REVISIÓN, NECESITA INFORMACIÓN)
     */
    public List<ExceptionalCaseResponseDTO> getStudentActiveCases(String studentId) {
        List<ExceptionalCase> activeCases = exceptionalCaseRepository.findActiveCasesByStudent(studentId);
        return activeCases.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Buscar caso por número de caso
     */
    public ExceptionalCaseResponseDTO getCaseByNumber(String caseNumber, String userId, String userRole) {
        ExceptionalCase exceptionalCase = exceptionalCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new NotFoundException("Caso no encontrado"));

        // Validar permisos de acceso
        validateCaseAccess(exceptionalCase, userId, userRole);

        return mapToDTO(exceptionalCase);
    }

    /**
     * Validar acceso al caso
     */
    private void validateCaseAccess(ExceptionalCase exceptionalCase, String userId, String userRole) {
        if ("STUDENT".equals(userRole)) {
            if (!exceptionalCase.getStudentId().equals(userId)) {
                throw new BusinessException("No tienes permiso para acceder a este caso");
            }
        } else if ("ADMIN".equals(userRole) || "DEAN".equals(userRole)) {
            if (!permissionService.canViewAllRequests(userId)) {
                throw new BusinessException("No tiene permisos para acceder a casos excepcionales");
            }
        } else {
            throw new BusinessException("Rol de usuario no válido");
        }
    }

    /**
     * Obtener casos recientes (últimos 30 días)
     */
    public List<ExceptionalCaseResponseDTO> getRecentCases(String employeeCode) {
        validateAdminAccess(employeeCode);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<ExceptionalCase> recentCases = exceptionalCaseRepository.findRecentCases(thirtyDaysAgo);
        return recentCases.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
}