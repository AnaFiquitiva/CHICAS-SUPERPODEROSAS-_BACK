package eci.edu.dosw.proyecto.service.impl;
import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.*;
import eci.edu.dosw.proyecto.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final StudentRepository studentRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final ValidationService validationService;
    private final GroupRepository groupRepository;
    private final RequestNumberGenerator requestNumberGenerator;
    private final RequestMapper requestMapper;

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

        // Crear la solicitud
        ChangeRequest changeRequest = buildChangeRequest(studentId, requestDTO);
        ChangeRequest savedRequest = changeRequestRepository.save(changeRequest);

        log.info("Solicitud creada exitosamente: {}", savedRequest.getRequestNumber());

        return requestMapper.toResponseDTO(savedRequest);
    }

    @Override
    public List<ChangeRequestResponseDTO> getStudentRequests(String studentId) {
        List<ChangeRequest> requests = changeRequestRepository.findByStudentId(studentId);
        return requestMapper.toResponseDTOs(requests);
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

    @Override
    @Transactional
    public ChangeRequestResponseDTO cancelRequest(String requestId, String studentId) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud no encontrada"));

        if (!request.getStudentId().equals(studentId)) {
            throw new BusinessException("No puedes cancelar una solicitud que no te pertenece");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BusinessException("Solo se pueden cancelar solicitudes pendientes");
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
        request.setStatus(RequestStatus.REJECTED);

        ChangeRequest updatedRequest = changeRequestRepository.save(request);

        return requestMapper.toResponseDTO(updatedRequest);
    }

    @Override
    public ValidationResponseDTO validateScheduleConflict(String studentId, String targetGroupId) {
        try {
            // Obtener el grupo objetivo
            Group targetGroup = groupRepository.findById(targetGroupId)
                    .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

            // Obtener las inscripciones activas del estudiante
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

            // Verificar conflictos con cada inscripción activa
            boolean hasConflict = student.getCurrentEnrollments().stream()
                    .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ACTIVE)
                    .map(Enrollment::getGroupId)
                    .map(groupId -> groupRepository.findById(groupId).orElse(null))
                    .filter(group -> group != null && group.getSchedule() != null)
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
                    activePeriods.stream().anyMatch(AcademicPeriod::getAllowGroupChanges);

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
                return new ValidationResponseDTO(false, "Límite excedido", "No hay períodos activos");
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
                        String.format("Has alcanzado el máximo de %d solicitudes activas", maxRequests));
            }

            return new ValidationResponseDTO(true, "Dentro del límite permitido", null);

        } catch (Exception e) {
            return new ValidationResponseDTO(false, "Error validando límite de solicitudes", e.getMessage());
        }
    }

    private ChangeRequest buildChangeRequest(String studentId, ChangeRequestRequestDTO requestDTO) {
        ChangeRequest request = requestMapper.toEntity(requestDTO);
        request.setRequestNumber(requestNumberGenerator.generateRequestNumber());
        request.setStudentId(studentId);
        request.setCreationDate(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        request.setPriority(1);

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
}