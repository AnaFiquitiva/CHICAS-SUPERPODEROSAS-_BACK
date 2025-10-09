package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.*;
import eci.edu.dosw.proyecto.utils.RequestMapper;
import eci.edu.dosw.proyecto.utils.RequestNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final StudentRepository studentRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final ValidationService validationService;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RequestNumberGenerator requestNumberGenerator;
    private final RequestMapper requestMapper;
    private final PermissionService permissionService;
    private final EnrollmentService enrollmentService;

    // ========== MÉTODOS DE CREACIÓN Y GESTIÓN ==========

    @Override
    @Transactional
    public ChangeRequestResponseDTO createChangeRequest(String studentId, ChangeRequestRequestDTO requestDTO) {
        log.info("Creando solicitud de cambio para estudiante: {}", studentId);

        // Validaciones previas
        validationService.validateStudentEligibility(studentId);
        validationService.validateRequest(requestDTO, studentId);

        ValidationResponseDTO periodValidation = validateAcademicPeriodActive();
        if (!periodValidation.isValid()) {
            throw new BusinessException(periodValidation.getMessage());
        }

        ValidationResponseDTO maxRequestsValidation = validateMaxRequests(studentId);
        if (!maxRequestsValidation.isValid()) {
            throw new BusinessException(maxRequestsValidation.getMessage());
        }

        // Validar conflicto de horario para cambios que involucren grupos
        if (requestDTO.getTargetGroupId() != null) {
            ValidationResponseDTO scheduleValidation = validateScheduleConflict(studentId, requestDTO.getTargetGroupId());
            if (!scheduleValidation.isValid()) {
                throw new BusinessException(scheduleValidation.getMessage());
            }
        }

        // Crear la solicitud
        ChangeRequest changeRequest = buildChangeRequest(studentId, requestDTO);
        ChangeRequest savedRequest = changeRequestRepository.save(changeRequest);

        log.info("Solicitud creada exitosamente: {}", savedRequest.getRequestNumber());

        return requestMapper.toResponseDTO(savedRequest);
    }

    @Override
    @Transactional
    public ChangeRequestResponseDTO cancelRequest(String requestId, String studentId) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        if (!request.getStudentId().equals(studentId)) {
            throw new BusinessException("No puedes cancelar una solicitud que no te pertenece");
        }

        if (request.getStatus() != RequestStatus.PENDING && request.getStatus() != RequestStatus.UNDER_REVIEW) {
            throw new BusinessException("Solo se pueden cancelar solicitudes pendientes o en revisión");
        }

        // Agregar al historial
        RequestHistory cancelHistory = new RequestHistory();
        cancelHistory.setTimestamp(LocalDateTime.now());
        cancelHistory.setAction("CANCELLED");
        cancelHistory.setDescription("Solicitud cancelada por el estudiante");
        cancelHistory.setUserId(studentId);
        cancelHistory.setUserRole("STUDENT");

        if (request.getHistory() == null) {
            request.setHistory(new ArrayList<>());
        }
        request.getHistory().add(cancelHistory);
        request.setStatus(RequestStatus.CANCELLED);

        ChangeRequest updatedRequest = changeRequestRepository.save(request);
        log.info("Solicitud {} cancelada por estudiante {}", requestId, studentId);

        return requestMapper.toResponseDTO(updatedRequest);
    }

    // ========== MÉTODOS DE APROBACIÓN, RECHAZO Y GESTIÓN ==========

    @Override
    @Transactional
    public ChangeRequestResponseDTO approveRequest(String requestId, String employeeCode, String comments) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        // Validar que la solicitud esté en estado apropiado
        if (request.getStatus() != RequestStatus.PENDING && request.getStatus() != RequestStatus.UNDER_REVIEW) {
            throw new BusinessException("Solo se pueden aprobar solicitudes pendientes o en revisión");
        }

        // Validar permisos según el tipo de solicitud
        if (request.getType() == RequestType.PLAN_CHANGE) {
            if (!permissionService.canApprovePlanChanges(employeeCode)) {
                throw new BusinessException("No tiene permisos para aprobar cambios de plan");
            }
        } else {
            if (!permissionService.canViewAllRequests(employeeCode)) {
                throw new BusinessException("No tiene permisos para aprobar solicitudes");
            }
        }

        // Actualizar la solicitud
        request.setStatus(RequestStatus.APPROVED);
        request.setAssignedTo(employeeCode);
        request.setReviewDate(LocalDateTime.now());
        request.setReviewComments(comments);
        request.setRejectionReason(null);
        request.setRequiredInformation(null);
        request.setResponseDeadline(null);

        // Agregar al historial
        RequestHistory approvalHistory = new RequestHistory();
        approvalHistory.setTimestamp(LocalDateTime.now());
        approvalHistory.setAction("APPROVED");
        approvalHistory.setDescription("Solicitud aprobada por " + employeeCode);
        approvalHistory.setUserId(employeeCode);
        approvalHistory.setUserRole("ADMIN/DEAN");
        approvalHistory.setComments(comments);

        if (request.getHistory() == null) {
            request.setHistory(new ArrayList<>());
        }
        request.getHistory().add(approvalHistory);

        // Ejecutar la acción correspondiente según el tipo de solicitud
        try {
            executeApprovalAction(request);
            request.setStatus(RequestStatus.COMPLETED);
        } catch (BusinessException e) {
            log.error("Error ejecutando acción de aprobación para solicitud {}: {}", requestId, e.getMessage());
            // Revertir el estado a UNDER_REVIEW si hay error en la ejecución
            request.setStatus(RequestStatus.UNDER_REVIEW);

            RequestHistory errorHistory = new RequestHistory();
            errorHistory.setTimestamp(LocalDateTime.now());
            errorHistory.setAction("EXECUTION_ERROR");
            errorHistory.setDescription("Error ejecutando la acción de aprobación: " + e.getMessage());
            errorHistory.setUserId(employeeCode);
            errorHistory.setUserRole("SYSTEM");
            request.getHistory().add(errorHistory);
        }

        ChangeRequest updatedRequest = changeRequestRepository.save(request);
        log.info("Solicitud {} aprobada por {}", requestId, employeeCode);

        return requestMapper.toResponseDTO(updatedRequest);
    }

    @Override
    @Transactional
    public ChangeRequestResponseDTO rejectRequest(String requestId, String employeeCode, String reason, String comments) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        // Validar que la solicitud esté en estado apropiado
        if (request.getStatus() != RequestStatus.PENDING && request.getStatus() != RequestStatus.UNDER_REVIEW) {
            throw new BusinessException("Solo se pueden rechazar solicitudes pendientes o en revisión");
        }

        // Validar permisos
        if (!permissionService.canViewAllRequests(employeeCode)) {
            throw new BusinessException("No tiene permisos para rechazar solicitudes");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("La razón del rechazo es obligatoria");
        }

        // Actualizar la solicitud
        request.setStatus(RequestStatus.REJECTED);
        request.setAssignedTo(employeeCode);
        request.setReviewDate(LocalDateTime.now());
        request.setReviewComments(comments);
        request.setRejectionReason(reason);
        request.setRequiredInformation(null);
        request.setResponseDeadline(null);

        // Agregar al historial
        RequestHistory rejectionHistory = new RequestHistory();
        rejectionHistory.setTimestamp(LocalDateTime.now());
        rejectionHistory.setAction("REJECTED");
        rejectionHistory.setDescription("Solicitud rechazada por " + employeeCode);
        rejectionHistory.setUserId(employeeCode);
        rejectionHistory.setUserRole("ADMIN/DEAN");
        rejectionHistory.setComments("Razón: " + reason + (comments != null ? ". " + comments : ""));

        if (request.getHistory() == null) {
            request.setHistory(new ArrayList<>());
        }
        request.getHistory().add(rejectionHistory);

        ChangeRequest updatedRequest = changeRequestRepository.save(request);
        log.info("Solicitud {} rechazada por {} - razón: {}", requestId, employeeCode, reason);

        return requestMapper.toResponseDTO(updatedRequest);
    }

    @Override
    @Transactional
    public ChangeRequestResponseDTO requestMoreInformation(String requestId, String employeeCode, String informationNeeded, Integer daysToRespond) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        // Validar que la solicitud esté en estado apropiado
        if (request.getStatus() != RequestStatus.PENDING && request.getStatus() != RequestStatus.UNDER_REVIEW) {
            throw new BusinessException("Solo se puede solicitar información para solicitudes pendientes o en revisión");
        }

        // Validar permisos
        if (!permissionService.canViewAllRequests(employeeCode)) {
            throw new BusinessException("No tiene permisos para solicitar información");
        }

        if (informationNeeded == null || informationNeeded.trim().isEmpty()) {
            throw new BusinessException("La información requerida es obligatoria");
        }

        // Actualizar la solicitud
        request.setStatus(RequestStatus.NEEDS_INFO);
        request.setAssignedTo(employeeCode);
        request.setReviewDate(LocalDateTime.now());
        request.setRequiredInformation(informationNeeded);
        request.setResponseDeadline(LocalDateTime.now().plusDays(daysToRespond != null ? daysToRespond : 5));
        request.setRejectionReason(null);

        // Agregar al historial
        RequestHistory infoRequestHistory = new RequestHistory();
        infoRequestHistory.setTimestamp(LocalDateTime.now());
        infoRequestHistory.setAction("INFO_REQUESTED");
        infoRequestHistory.setDescription("Información adicional solicitada por " + employeeCode);
        infoRequestHistory.setUserId(employeeCode);
        infoRequestHistory.setUserRole("ADMIN/DEAN");
        infoRequestHistory.setComments("Información requerida: " + informationNeeded +
                ". Fecha límite: " + request.getResponseDeadline().toLocalDate());

        if (request.getHistory() == null) {
            request.setHistory(new ArrayList<>());
        }
        request.getHistory().add(infoRequestHistory);

        ChangeRequest updatedRequest = changeRequestRepository.save(request);
        log.info("Información solicitada para solicitud {} por {} - información: {}",
                requestId, employeeCode, informationNeeded);

        return requestMapper.toResponseDTO(updatedRequest);
    }

    @Override
    @Transactional
    public ChangeRequestResponseDTO provideAdditionalInfo(String requestId, String studentId, String additionalInfo) {
        ChangeRequest request = changeRequestRepository.findByStudentIdAndId(studentId, requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada o no pertenece al estudiante"));

        // Validar que la solicitud requiera información
        if (request.getStatus() != RequestStatus.NEEDS_INFO) {
            throw new BusinessException("Esta solicitud no requiere información adicional");
        }

        // Validar que no haya expirado el plazo
        if (request.getResponseDeadline() != null && LocalDateTime.now().isAfter(request.getResponseDeadline())) {
            throw new BusinessException("El plazo para proporcionar información ha expirado");
        }

        if (additionalInfo == null || additionalInfo.trim().isEmpty()) {
            throw new BusinessException("La información adicional es obligatoria");
        }

        // Actualizar la solicitud
        request.setStatus(RequestStatus.UNDER_REVIEW);
        request.setRequiredInformation(request.getRequiredInformation() +
                "\n\n--- INFORMACIÓN PROPORCIONADA POR EL ESTUDIANTE ---\n" +
                "Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                "\nInformación:\n" + additionalInfo);

        // Agregar al historial
        RequestHistory infoProvidedHistory = new RequestHistory();
        infoProvidedHistory.setTimestamp(LocalDateTime.now());
        infoProvidedHistory.setAction("INFO_PROVIDED");
        infoProvidedHistory.setDescription("Información adicional proporcionada por el estudiante");
        infoProvidedHistory.setUserId(studentId);
        infoProvidedHistory.setUserRole("STUDENT");
        infoProvidedHistory.setComments("Información proporcionada: " +
                (additionalInfo.length() > 100 ? additionalInfo.substring(0, 100) + "..." : additionalInfo));

        if (request.getHistory() == null) {
            request.setHistory(new ArrayList<>());
        }
        request.getHistory().add(infoProvidedHistory);

        ChangeRequest updatedRequest = changeRequestRepository.save(request);
        log.info("Información adicional proporcionada para solicitud {} por estudiante {}", requestId, studentId);

        return requestMapper.toResponseDTO(updatedRequest);
    }

    // ========== MÉTODOS DE CONSULTA PARA ESTUDIANTES ==========

    @Override
    public List<ChangeRequestResponseDTO> getStudentRequests(String studentId) {
        List<ChangeRequest> requests = changeRequestRepository.findByStudentId(studentId);
        log.debug("Encontradas {} solicitudes para estudiante {}", requests.size(), studentId);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getStudentRequestsByStatus(String studentId, RequestStatus status) {
        List<ChangeRequest> requests = changeRequestRepository.findByStudentIdAndStatus(studentId, status);
        log.debug("Encontradas {} solicitudes con estado {} para estudiante {}", requests.size(), status, studentId);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getStudentRequestsByType(String studentId, RequestType type) {
        List<ChangeRequest> requests = changeRequestRepository.findByStudentIdAndType(studentId, type);
        log.debug("Encontradas {} solicitudes de tipo {} para estudiante {}", requests.size(), type, studentId);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getActiveRequestsByStudent(String studentId) {
        List<ChangeRequest> requests = changeRequestRepository.findActiveRequestsByStudent(studentId);
        log.debug("Encontradas {} solicitudes activas para estudiante {}", requests.size(), studentId);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public ChangeRequestResponseDTO getStudentRequestById(String studentId, String requestId) {
        ChangeRequest request = changeRequestRepository.findByStudentIdAndId(studentId, requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada o no pertenece al estudiante"));
        return requestMapper.toResponseDTO(request);
    }

    @Override
    public ChangeRequestResponseDTO getStudentRequestByNumber(String studentId, String requestNumber) {
        ChangeRequest request = changeRequestRepository.findByStudentIdAndRequestNumber(studentId, requestNumber)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada o no pertenece al estudiante"));
        return requestMapper.toResponseDTO(request);
    }

    @Override
    public ChangeRequestResponseDTO getRequestById(String requestId) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));
        return requestMapper.toResponseDTO(request);
    }

    @Override
    public ChangeRequestResponseDTO getRequestByNumber(String requestNumber) {
        ChangeRequest request = changeRequestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));
        return requestMapper.toResponseDTO(request);
    }

    // ========== MÉTODOS DE CONSULTA PARA ADMINISTRADORES ==========

    @Override
    public List<ChangeRequestResponseDTO> getAllRequests(RequestStatus status, RequestType type, String studentId, String periodName) {
        List<ChangeRequest> requests;

        if (studentId != null) {
            requests = changeRequestRepository.findByStudentId(studentId);
        } else {
            requests = changeRequestRepository.findAll();
        }

        // Aplicar filtros
        List<ChangeRequest> filteredRequests = requests.stream()
                .filter(request -> status == null || request.getStatus() == status)
                .filter(request -> type == null || request.getType() == type)
                .filter(request -> {
                    if (periodName == null) return true;
                    try {
                        AcademicPeriod period = academicPeriodRepository.findByPeriodName(periodName).orElse(null);
                        return period != null &&
                                !request.getCreationDate().isBefore(period.getStartDate()) &&
                                !request.getCreationDate().isAfter(period.getEndDate());
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        log.debug("Encontradas {} solicitudes con filtros: status={}, type={}, studentId={}, periodName={}",
                filteredRequests.size(), status, type, studentId, periodName);

        return requestMapper.toResponseDTOs(filteredRequests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getRequestsByStatus(RequestStatus status) {
        List<ChangeRequest> requests = changeRequestRepository.findByStatus(status);
        log.debug("Encontradas {} solicitudes con estado {}", requests.size(), status);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getRequestsByType(RequestType type) {
        List<ChangeRequest> requests = changeRequestRepository.findByType(type);
        log.debug("Encontradas {} solicitudes de tipo {}", requests.size(), type);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getRequestsByPeriod(String periodName) {
        AcademicPeriod period = academicPeriodRepository.findByPeriodName(periodName)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado"));

        List<ChangeRequest> requests = changeRequestRepository.findByCreationDateBetween(
                period.getStartDate(), period.getEndDate());

        log.debug("Encontradas {} solicitudes para el período {}", requests.size(), periodName);
        return requestMapper.toResponseDTOs(requests);
    }

    @Override
    public List<ChangeRequestResponseDTO> getRecentRequests() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<ChangeRequest> requests = changeRequestRepository.findByCreationDateAfter(oneWeekAgo);
        log.debug("Encontradas {} solicitudes recientes (última semana)", requests.size());
        return requestMapper.toResponseDTOs(requests);
    }

    // ========== MÉTODOS DE ESTADÍSTICAS ==========

    @Override
    public RequestStatsDTO getRequestStats() {
        RequestStatsDTO stats = new RequestStatsDTO();

        // Estadísticas básicas
        stats.setTotalRequests(changeRequestRepository.countTotalRequests());
        stats.setPendingRequests(changeRequestRepository.countByStatus(RequestStatus.PENDING));
        stats.setUnderReviewRequests(changeRequestRepository.countByStatus(RequestStatus.UNDER_REVIEW));
        stats.setApprovedRequests(changeRequestRepository.countByStatus(RequestStatus.APPROVED));
        stats.setRejectedRequests(changeRequestRepository.countByStatus(RequestStatus.REJECTED));
        stats.setNeedsInfoRequests(changeRequestRepository.countByStatus(RequestStatus.NEEDS_INFO));

        // Estadísticas por tipo
        stats.setGroupChangeRequests(changeRequestRepository.countByType(RequestType.GROUP_CHANGE));
        stats.setSubjectChangeRequests(changeRequestRepository.countByType(RequestType.SUBJECT_CHANGE));
        stats.setPlanChangeRequests(changeRequestRepository.countByType(RequestType.PLAN_CHANGE));
        stats.setNewEnrollmentRequests(changeRequestRepository.countByType(RequestType.NEW_ENROLLMENT));

        // Solicitudes por tiempo
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        stats.setRequestsThisWeek(changeRequestRepository.countByCreationDateAfter(oneWeekAgo));
        stats.setRequestsThisMonth(changeRequestRepository.countByCreationDateAfter(oneMonthAgo));
        stats.setRequestsThisYear(changeRequestRepository.countByCreationDateAfter(oneYearAgo));

        // Cálculo de tasas
        long totalProcessed = stats.getApprovedRequests() + stats.getRejectedRequests();
        if (totalProcessed > 0) {
            stats.setApprovalRate(Math.round((double) stats.getApprovedRequests() / totalProcessed * 100 * 100.0) / 100.0);
            stats.setRejectionRate(Math.round((double) stats.getRejectedRequests() / totalProcessed * 100 * 100.0) / 100.0);
        } else {
            stats.setApprovalRate(0.0);
            stats.setRejectionRate(0.0);
        }

        if (stats.getTotalRequests() > 0) {
            stats.setPendingRate(Math.round((double) stats.getPendingRequests() / stats.getTotalRequests() * 100 * 100.0) / 100.0);
        } else {
            stats.setPendingRate(0.0);
        }

        // Información del período actual
        List<AcademicPeriod> activePeriods = academicPeriodRepository.findActivePeriods(LocalDateTime.now());
        if (!activePeriods.isEmpty()) {
            stats.setCurrentActivePeriod(activePeriods.get(0).getPeriodName());
        }

        // Solicitudes urgentes
        List<ChangeRequest> urgentRequests = changeRequestRepository.findUrgentRequests();
        stats.setUrgentRequests((long) urgentRequests.size());
        stats.setNormalRequests(stats.getTotalRequests() - stats.getUrgentRequests());

        // Calcular tiempo promedio de procesamiento
        stats.setAverageProcessingTimeDays(calculateAverageProcessingTime());

        log.debug("Estadísticas generales generadas: {} solicitudes totales", stats.getTotalRequests());
        return stats;
    }

    @Override
    public RequestStatsDTO getDetailedStats(String periodName, RequestType type, String program) {
        RequestStatsDTO stats = new RequestStatsDTO();

        List<ChangeRequest> filteredRequests = changeRequestRepository.findAll();

        // Aplicar filtros
        if (periodName != null) {
            AcademicPeriod period = academicPeriodRepository.findByPeriodName(periodName).orElse(null);
            if (period != null) {
                filteredRequests = filteredRequests.stream()
                        .filter(request -> !request.getCreationDate().isBefore(period.getStartDate()) &&
                                !request.getCreationDate().isAfter(period.getEndDate()))
                        .collect(Collectors.toList());
            }
        }

        if (type != null) {
            filteredRequests = filteredRequests.stream()
                    .filter(request -> request.getType() == type)
                    .collect(Collectors.toList());
        }

        if (program != null) {
            filteredRequests = filteredRequests.stream()
                    .filter(request -> {
                        try {
                            Student student = studentRepository.findById(request.getStudentId()).orElse(null);
                            return student != null && program.equals(student.getProgram());
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // Calcular estadísticas con los datos filtrados
        stats.setTotalRequests((long) filteredRequests.size());
        stats.setPendingRequests(filteredRequests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count());
        stats.setUnderReviewRequests(filteredRequests.stream().filter(r -> r.getStatus() == RequestStatus.UNDER_REVIEW).count());
        stats.setApprovedRequests(filteredRequests.stream().filter(r -> r.getStatus() == RequestStatus.APPROVED).count());
        stats.setRejectedRequests(filteredRequests.stream().filter(r -> r.getStatus() == RequestStatus.REJECTED).count());
        stats.setNeedsInfoRequests(filteredRequests.stream().filter(r -> r.getStatus() == RequestStatus.NEEDS_INFO).count());

        stats.setGroupChangeRequests(filteredRequests.stream().filter(r -> r.getType() == RequestType.GROUP_CHANGE).count());
        stats.setSubjectChangeRequests(filteredRequests.stream().filter(r -> r.getType() == RequestType.SUBJECT_CHANGE).count());
        stats.setPlanChangeRequests(filteredRequests.stream().filter(r -> r.getType() == RequestType.PLAN_CHANGE).count());
        stats.setNewEnrollmentRequests(filteredRequests.stream().filter(r -> r.getType() == RequestType.NEW_ENROLLMENT).count());

        // Calcular tiempos promedio de procesamiento
        long totalProcessingTime = filteredRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.REJECTED)
                .mapToLong(r -> {
                    Optional<RequestHistory> creation = r.getHistory().stream()
                            .filter(h -> "CREATED".equals(h.getAction()))
                            .findFirst();
                    Optional<RequestHistory> resolution = r.getHistory().stream()
                            .filter(h -> "APPROVED".equals(h.getAction()) || "REJECTED".equals(h.getAction()))
                            .findFirst();

                    if (creation.isPresent() && resolution.isPresent()) {
                        return ChronoUnit.DAYS.between(creation.get().getTimestamp(), resolution.get().getTimestamp());
                    }
                    return 0;
                })
                .sum();

        long processedCount = filteredRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.REJECTED)
                .count();

        stats.setAverageProcessingTimeDays(processedCount > 0 ? totalProcessingTime / processedCount : 0);

        // Solicitudes urgentes
        stats.setUrgentRequests(filteredRequests.stream().filter(r -> r.getPriority() != null && r.getPriority() > 1).count());
        stats.setNormalRequests(filteredRequests.stream().filter(r -> r.getPriority() == null || r.getPriority() == 1).count());

        // Calcular tasas
        long totalProcessed = stats.getApprovedRequests() + stats.getRejectedRequests();
        if (totalProcessed > 0) {
            stats.setApprovalRate(Math.round((double) stats.getApprovedRequests() / totalProcessed * 100 * 100.0) / 100.0);
            stats.setRejectionRate(Math.round((double) stats.getRejectedRequests() / totalProcessed * 100 * 100.0) / 100.0);
        }

        if (stats.getTotalRequests() > 0) {
            stats.setPendingRate(Math.round((double) stats.getPendingRequests() / stats.getTotalRequests() * 100 * 100.0) / 100.0);
        }

        log.debug("Estadísticas detalladas generadas: {} solicitudes filtradas", filteredRequests.size());
        return stats;
    }

    @Override
    public StudentRequestStatsDTO getStudentRequestStats(String studentId) {
        StudentRequestStatsDTO stats = new StudentRequestStatsDTO();

        // Verificar que el estudiante existe
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        // Estadísticas básicas
        stats.setTotalRequests(changeRequestRepository.countByStudentId(studentId));
        stats.setPendingRequests(changeRequestRepository.countByStudentIdAndStatus(studentId, RequestStatus.PENDING));
        stats.setUnderReviewRequests(changeRequestRepository.countByStudentIdAndStatus(studentId, RequestStatus.UNDER_REVIEW));
        stats.setApprovedRequests(changeRequestRepository.countByStudentIdAndStatus(studentId, RequestStatus.APPROVED));
        stats.setRejectedRequests(changeRequestRepository.countByStudentIdAndStatus(studentId, RequestStatus.REJECTED));
        stats.setNeedsInfoRequests(changeRequestRepository.countByStudentIdAndStatus(studentId, RequestStatus.NEEDS_INFO));

        // Estadísticas por tipo
        stats.setGroupChangeRequests(changeRequestRepository.countByStudentIdAndType(studentId, RequestType.GROUP_CHANGE));
        stats.setSubjectChangeRequests(changeRequestRepository.countByStudentIdAndType(studentId, RequestType.SUBJECT_CHANGE));
        stats.setPlanChangeRequests(changeRequestRepository.countByStudentIdAndType(studentId, RequestType.PLAN_CHANGE));
        stats.setNewEnrollmentRequests(changeRequestRepository.countByStudentIdAndType(studentId, RequestType.NEW_ENROLLMENT));

        // Cálculo de tasas
        long totalProcessed = stats.getApprovedRequests() + stats.getRejectedRequests();
        if (totalProcessed > 0) {
            stats.setApprovalRate(Math.round((double) stats.getApprovedRequests() / totalProcessed * 100 * 100.0) / 100.0);
            stats.setRejectionRate(Math.round((double) stats.getRejectedRequests() / totalProcessed * 100 * 100.0) / 100.0);
        } else {
            stats.setApprovalRate(0.0);
            stats.setRejectionRate(0.0);
        }

        // Calcular tiempo promedio de procesamiento para el estudiante
        List<ChangeRequest> studentRequests = changeRequestRepository.findByStudentId(studentId);
        long totalProcessingTime = studentRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.REJECTED)
                .mapToLong(r -> {
                    Optional<RequestHistory> creation = r.getHistory().stream()
                            .filter(h -> "CREATED".equals(h.getAction()))
                            .findFirst();
                    Optional<RequestHistory> resolution = r.getHistory().stream()
                            .filter(h -> "APPROVED".equals(h.getAction()) || "REJECTED".equals(h.getAction()))
                            .findFirst();

                    if (creation.isPresent() && resolution.isPresent()) {
                        return ChronoUnit.DAYS.between(creation.get().getTimestamp(), resolution.get().getTimestamp());
                    }
                    return 0;
                })
                .sum();

        long processedCount = studentRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.REJECTED)
                .count();

        stats.setAverageProcessingDays(processedCount > 0 ? totalProcessingTime / processedCount : 0);

        // Límites y disponibilidad
        try {
            List<AcademicPeriod> activePeriods = academicPeriodRepository.findActivePeriods(LocalDateTime.now());

            if (!activePeriods.isEmpty()) {
                AcademicPeriod activePeriod = activePeriods.get(0);
                stats.setCurrentAcademicPeriod(activePeriod.getPeriodName());
                stats.setIsPeriodActive(true);

                Integer maxRequests = activePeriod.getMaxRequestsPerStudent();

                if (maxRequests != null) {
                    Long currentActiveRequests = changeRequestRepository.countByStudentIdAndStatusIn(
                            studentId, List.of("PENDING", "UNDER_REVIEW"));

                    stats.setMaxRequestsAllowed(maxRequests);
                    stats.setRemainingRequests(Math.max(0, maxRequests - currentActiveRequests.intValue()));
                    stats.setCanCreateNewRequest(currentActiveRequests < maxRequests);

                    if (stats.getCanCreateNewRequest()) {
                        stats.setAvailabilityMessage(String.format("Puedes crear %d solicitudes más", stats.getRemainingRequests()));
                    } else {
                        stats.setAvailabilityMessage("Has alcanzado el límite de solicitudes activas");
                    }
                } else {
                    stats.setCanCreateNewRequest(true);
                    stats.setAvailabilityMessage("Sin límite de solicitudes configurado");
                    stats.setMaxRequestsAllowed(0);
                    stats.setRemainingRequests(0);
                }
            } else {
                stats.setIsPeriodActive(false);
                stats.setCanCreateNewRequest(false);
                stats.setAvailabilityMessage("No hay períodos académicos activos");
                stats.setMaxRequestsAllowed(0);
                stats.setRemainingRequests(0);
            }
        } catch (Exception e) {
            log.warn("Error calculando límites para estudiante {}: {}", studentId, e.getMessage());
            stats.setCanCreateNewRequest(false);
            stats.setAvailabilityMessage("Error verificando disponibilidad");
            stats.setMaxRequestsAllowed(0);
            stats.setRemainingRequests(0);
        }

        log.debug("Estadísticas del estudiante {} generadas: {} solicitudes totales", studentId, stats.getTotalRequests());
        return stats;
    }

    // ========== MÉTODOS DE VALIDACIÓN ==========

    @Override
    public ValidationResponseDTO validateScheduleConflict(String studentId, String targetGroupId) {
        try {
            Group targetGroup = groupRepository.findById(targetGroupId)
                    .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

            boolean hasConflict = student.getCurrentEnrollments().stream()
                    .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ACTIVE)
                    .map(Enrollment::getGroupId)
                    .map(groupId -> groupRepository.findById(groupId).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(group -> group.getSchedule() != null && targetGroup.getSchedule() != null)
                    .anyMatch(group -> group.getSchedule().hasConflict(targetGroup.getSchedule()));

            if (hasConflict) {
                return new ValidationResponseDTO(false, "Conflicto de horario detectado",
                        "El grupo objetivo tiene conflicto con otras materias inscritas");
            }

            return new ValidationResponseDTO(true, "No hay conflictos de horario", null);

        } catch (Exception e) {
            return new ValidationResponseDTO(false, "Error validando horario", e.getMessage());
        }
    }

    @Override
    public ValidationResponseDTO validateAcademicPeriodActive() {
        try {
            List<AcademicPeriod> activePeriods = academicPeriodRepository.findActivePeriods(LocalDateTime.now());
            boolean isActive = !activePeriods.isEmpty() &&
                    activePeriods.stream().anyMatch(period ->
                            Boolean.TRUE.equals(period.getAllowGroupChanges()) ||
                                    Boolean.TRUE.equals(period.getAllowSubjectChanges()));

            if (!isActive) {
                return new ValidationResponseDTO(false, "Período académico inactivo",
                        "No hay un período académico activo para realizar cambios");
            }

            return new ValidationResponseDTO(true, "Período académico activo", null);

        } catch (Exception e) {
            return new ValidationResponseDTO(false, "Error validando período académico", e.getMessage());
        }
    }

    @Override
    public ValidationResponseDTO validateMaxRequests(String studentId) {
        try {
            List<AcademicPeriod> activePeriods = academicPeriodRepository.findActivePeriods(LocalDateTime.now());
            if (activePeriods.isEmpty()) {
                return new ValidationResponseDTO(false, "No hay períodos activos",
                        "No se pueden crear solicitudes sin un período académico activo");
            }

            AcademicPeriod activePeriod = activePeriods.get(0);
            Integer maxRequests = activePeriod.getMaxRequestsPerStudent();

            if (maxRequests == null) {
                return new ValidationResponseDTO(true, "Sin límite configurado", null);
            }

            Long currentRequests = changeRequestRepository.countByStudentIdAndStatusIn(
                    studentId,
                    List.of("PENDING", "UNDER_REVIEW")
            );

            if (currentRequests >= maxRequests) {
                return new ValidationResponseDTO(false, "Límite de solicitudes alcanzado",
                        String.format("Has alcanzado el máximo de %d solicitudes activas. Actualmente tienes %d.",
                                maxRequests, currentRequests));
            }

            return new ValidationResponseDTO(true, "Dentro del límite permitido",
                    String.format("Puedes crear %d solicitudes más", maxRequests - currentRequests));

        } catch (Exception e) {
            return new ValidationResponseDTO(false, "Error validando límite de solicitudes", e.getMessage());
        }
    }

    // ========== MÉTODOS PRIVADOS ==========

    private ChangeRequest buildChangeRequest(String studentId, ChangeRequestRequestDTO requestDTO) {
        ChangeRequest request = requestMapper.toEntity(requestDTO);
        request.setRequestNumber(requestNumberGenerator.generateRequestNumber());
        request.setStudentId(studentId);
        request.setCreationDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        request.setPriority(calculatePriority(requestDTO));

        // Mapear plan changes si existen
        if (requestDTO.getPlanChanges() != null) {
            request.setPlanChanges(requestMapper.toPlanChangeDetailEntities(requestDTO.getPlanChanges()));
        }

        // Construir historial inicial
        RequestHistory creationHistory = new RequestHistory();
        creationHistory.setTimestamp(LocalDateTime.now());
        creationHistory.setAction("CREATED");
        creationHistory.setDescription("Solicitud creada por el estudiante");
        creationHistory.setUserId(studentId);
        creationHistory.setUserRole("STUDENT");

        request.setHistory(List.of(creationHistory));

        return request;
    }

    private Integer calculatePriority(ChangeRequestRequestDTO requestDTO) {
        // Lógica para calcular prioridad basada en el tipo de solicitud y circunstancias
        return switch (requestDTO.getType()) {
            case PLAN_CHANGE -> 2; // Alta prioridad
            case SUBJECT_CHANGE, GROUP_CHANGE -> 1; // Prioridad normal
            case NEW_ENROLLMENT -> 1; // Prioridad normal
            default -> 1;
        };
    }

    private Long calculateAverageProcessingTime() {
        List<ChangeRequest> allRequests = changeRequestRepository.findAll();

        long totalProcessingTime = allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.REJECTED)
                .mapToLong(r -> {
                    Optional<RequestHistory> creation = r.getHistory().stream()
                            .filter(h -> "CREATED".equals(h.getAction()))
                            .findFirst();
                    Optional<RequestHistory> resolution = r.getHistory().stream()
                            .filter(h -> "APPROVED".equals(h.getAction()) || "REJECTED".equals(h.getAction()))
                            .findFirst();

                    if (creation.isPresent() && resolution.isPresent()) {
                        return ChronoUnit.DAYS.between(creation.get().getTimestamp(), resolution.get().getTimestamp());
                    }
                    return 0;
                })
                .sum();

        long processedCount = allRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED || r.getStatus() == RequestStatus.REJECTED)
                .count();

        return processedCount > 0 ? totalProcessingTime / processedCount : 0;
    }

    // ========== MÉTODOS DE EJECUCIÓN DE ACCIONES ==========

    private void executeApprovalAction(ChangeRequest request) {
        switch (request.getType()) {
            case GROUP_CHANGE:
                executeGroupChange(request);
                break;
            case SUBJECT_CHANGE:
                executeSubjectChange(request);
                break;
            case PLAN_CHANGE:
                executePlanChange(request);
                break;
            case NEW_ENROLLMENT:
                executeNewEnrollment(request);
                break;
            default:
                log.warn("Tipo de solicitud no reconocido: {}", request.getType());
        }
    }

    private void executeGroupChange(ChangeRequest request) {
        try {
            // Validar que existan los grupos
            Group currentGroup = groupRepository.findById(request.getCurrentGroupId())
                    .orElseThrow(() -> new BusinessException("Grupo actual no encontrado"));
            Group targetGroup = groupRepository.findById(request.getTargetGroupId())
                    .orElseThrow(() -> new BusinessException("Grupo objetivo no encontrado"));

            // Validar que sean de la misma materia
            if (!currentGroup.getSubjectId().equals(targetGroup.getSubjectId())) {
                throw new BusinessException("Los grupos deben ser de la misma materia");
            }

            // Buscar la inscripción actual
            Enrollment currentEnrollment = enrollmentRepository
                    .findByStudentIdAndGroupIdAndStatus(request.getStudentId(), request.getCurrentGroupId(), EnrollmentStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException("No se encontró la inscripción actual"));

            // Cancelar inscripción actual
            currentEnrollment.setStatus(EnrollmentStatus.CANCELLED);
            enrollmentRepository.save(currentEnrollment);

            // Actualizar contador del grupo actual
            currentGroup.setCurrentEnrollment(Math.max(0, currentGroup.getCurrentEnrollment() - 1));
            groupRepository.save(currentGroup);

            // Crear nueva inscripción
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudentId(request.getStudentId());
            newEnrollment.setSubjectId(targetGroup.getSubjectId());
            newEnrollment.setGroupId(request.getTargetGroupId());
            newEnrollment.setEnrollmentDate(LocalDateTime.now());
            newEnrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollmentRepository.save(newEnrollment);

            // Actualizar contador del grupo objetivo
            targetGroup.setCurrentEnrollment(targetGroup.getCurrentEnrollment() + 1);
            groupRepository.save(targetGroup);

            log.info("Cambio de grupo ejecutado para estudiante {}: {} -> {}",
                    request.getStudentId(), request.getCurrentGroupId(), request.getTargetGroupId());

        } catch (Exception e) {
            log.error("Error ejecutando cambio de grupo para solicitud {}: {}", request.getId(), e.getMessage());
            throw new BusinessException("Error al ejecutar el cambio de grupo: " + e.getMessage());
        }
    }

    private void executeSubjectChange(ChangeRequest request) {
        try {
            // Validar que existan los grupos y materias
            Group currentGroup = groupRepository.findById(request.getCurrentGroupId())
                    .orElseThrow(() -> new BusinessException("Grupo actual no encontrado"));
            Group targetGroup = groupRepository.findById(request.getTargetGroupId())
                    .orElseThrow(() -> new BusinessException("Grupo objetivo no encontrado"));

            Subject currentSubject = subjectRepository.findById(request.getCurrentSubjectId())
                    .orElseThrow(() -> new BusinessException("Materia actual no encontrada"));
            Subject targetSubject = subjectRepository.findById(request.getTargetSubjectId())
                    .orElseThrow(() -> new BusinessException("Materia objetivo no encontrada"));

            // Buscar la inscripción actual
            Enrollment currentEnrollment = enrollmentRepository
                    .findByStudentIdAndGroupIdAndStatus(request.getStudentId(), request.getCurrentGroupId(), EnrollmentStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException("No se encontró la inscripción actual"));

            // Cancelar inscripción actual
            currentEnrollment.setStatus(EnrollmentStatus.CANCELLED);
            enrollmentRepository.save(currentEnrollment);

            // Actualizar contador del grupo actual
            currentGroup.setCurrentEnrollment(Math.max(0, currentGroup.getCurrentEnrollment() - 1));
            groupRepository.save(currentGroup);

            // Crear nueva inscripción
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudentId(request.getStudentId());
            newEnrollment.setSubjectId(request.getTargetSubjectId());
            newEnrollment.setGroupId(request.getTargetGroupId());
            newEnrollment.setEnrollmentDate(LocalDateTime.now());
            newEnrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollmentRepository.save(newEnrollment);

            // Actualizar contador del grupo objetivo
            targetGroup.setCurrentEnrollment(targetGroup.getCurrentEnrollment() + 1);
            groupRepository.save(targetGroup);

            log.info("Cambio de materia ejecutado para estudiante {}: {} -> {}",
                    request.getStudentId(), request.getCurrentSubjectId(), request.getTargetSubjectId());

        } catch (Exception e) {
            log.error("Error ejecutando cambio de materia para solicitud {}: {}", request.getId(), e.getMessage());
            throw new BusinessException("Error al ejecutar el cambio de materia: " + e.getMessage());
        }
    }

    private void executePlanChange(ChangeRequest request) {
        try {
            if (request.getPlanChanges() != null) {
                for (PlanChangeDetail change : request.getPlanChanges()) {
                    switch (change.getAction().toUpperCase()) {
                        case "ADD":
                            // Agregar nueva materia
                            Group addGroup = groupRepository.findById(change.getGroupId())
                                    .orElseThrow(() -> new BusinessException("Grupo no encontrado para agregar"));

                            Enrollment newEnrollment = new Enrollment();
                            newEnrollment.setStudentId(request.getStudentId());
                            newEnrollment.setSubjectId(change.getSubjectId());
                            newEnrollment.setGroupId(change.getGroupId());
                            newEnrollment.setEnrollmentDate(LocalDateTime.now());
                            newEnrollment.setStatus(EnrollmentStatus.ACTIVE);
                            enrollmentRepository.save(newEnrollment);

                            addGroup.setCurrentEnrollment(addGroup.getCurrentEnrollment() + 1);
                            groupRepository.save(addGroup);
                            break;

                        case "REMOVE":
                            // Remover materia actual
                            Enrollment removeEnrollment = enrollmentRepository
                                    .findByStudentIdAndGroupIdAndStatus(request.getStudentId(), change.getGroupId(), EnrollmentStatus.ACTIVE)
                                    .orElseThrow(() -> new BusinessException("No se encontró la inscripción a remover"));

                            removeEnrollment.setStatus(EnrollmentStatus.CANCELLED);
                            enrollmentRepository.save(removeEnrollment);

                            Group removeGroup = groupRepository.findById(change.getGroupId())
                                    .orElseThrow(() -> new BusinessException("Grupo no encontrado para remover"));
                            removeGroup.setCurrentEnrollment(Math.max(0, removeGroup.getCurrentEnrollment() - 1));
                            groupRepository.save(removeGroup);
                            break;

                        case "REPLACE":
                            // Reemplazar materia
                            // Primero remover la actual
                            Enrollment replaceEnrollment = enrollmentRepository
                                    .findByStudentIdAndGroupIdAndStatus(request.getStudentId(), change.getGroupId(), EnrollmentStatus.ACTIVE)
                                    .orElseThrow(() -> new BusinessException("No se encontró la inscripción a reemplazar"));

                            replaceEnrollment.setStatus(EnrollmentStatus.CANCELLED);
                            enrollmentRepository.save(replaceEnrollment);

                            Group oldGroup = groupRepository.findById(change.getGroupId())
                                    .orElseThrow(() -> new BusinessException("Grupo actual no encontrado"));
                            oldGroup.setCurrentEnrollment(Math.max(0, oldGroup.getCurrentEnrollment() - 1));
                            groupRepository.save(oldGroup);

                            // Luego agregar la nueva
                            Group newGroup = groupRepository.findById(change.getReplacementGroupId())
                                    .orElseThrow(() -> new BusinessException("Grupo de reemplazo no encontrado"));

                            Enrollment replacementEnrollment = new Enrollment();
                            replacementEnrollment.setStudentId(request.getStudentId());
                            replacementEnrollment.setSubjectId(change.getReplacementSubjectId());
                            replacementEnrollment.setGroupId(change.getReplacementGroupId());
                            replacementEnrollment.setEnrollmentDate(LocalDateTime.now());
                            replacementEnrollment.setStatus(EnrollmentStatus.ACTIVE);
                            enrollmentRepository.save(replacementEnrollment);

                            newGroup.setCurrentEnrollment(newGroup.getCurrentEnrollment() + 1);
                            groupRepository.save(newGroup);
                            break;
                    }
                }
            }

            log.info("Cambio de plan ejecutado para estudiante {}", request.getStudentId());

        } catch (Exception e) {
            log.error("Error ejecutando cambio de plan para solicitud {}: {}", request.getId(), e.getMessage());
            throw new BusinessException("Error al ejecutar el cambio de plan: " + e.getMessage());
        }
    }

    private void executeNewEnrollment(ChangeRequest request) {
        try {
            // Validar que exista el grupo
            Group targetGroup = groupRepository.findById(request.getTargetGroupId())
                    .orElseThrow(() -> new BusinessException("Grupo objetivo no encontrado"));

            // Crear nueva inscripción
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setStudentId(request.getStudentId());
            newEnrollment.setSubjectId(request.getTargetSubjectId());
            newEnrollment.setGroupId(request.getTargetGroupId());
            newEnrollment.setEnrollmentDate(LocalDateTime.now());
            newEnrollment.setStatus(EnrollmentStatus.ACTIVE);
            enrollmentRepository.save(newEnrollment);

            // Actualizar contador del grupo
            targetGroup.setCurrentEnrollment(targetGroup.getCurrentEnrollment() + 1);
            groupRepository.save(targetGroup);

            log.info("Nueva inscripción ejecutada para estudiante {} en grupo {}",
                    request.getStudentId(), request.getTargetGroupId());

        } catch (Exception e) {
            log.error("Error ejecutando nueva inscripción para solicitud {}: {}", request.getId(), e.getMessage());
            throw new BusinessException("Error al ejecutar la nueva inscripción: " + e.getMessage());
        }
    }
}