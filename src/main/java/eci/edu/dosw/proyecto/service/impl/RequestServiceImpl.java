package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.*;
import eci.edu.dosw.proyecto.utils.mappers.RequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static eci.edu.dosw.proyecto.service.impl.GroupServiceImpl.getUser;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final DeanRepository deanRepository;
    private final RequestHistoryRepository requestHistoryRepository;
    private final NotificationService notificationService;
    private final GroupService groupService;
    private final AcademicService academicService;
    private final RequestMapper requestMapper;
    private final StudentService studentService;

    @Override
    @Transactional
    public RequestResponse createRequest(RequestCreateRequest dto) {
        validateRequest(dto);

        Student student = studentRepository.findById(String.valueOf(dto.getStudentId()))
                .orElseThrow(() -> new NotFoundException("Estudiante", String.valueOf(dto.getStudentId())));

        Group requestedGroup = groupRepository.findById(dto.getRequestedGroupId())
                .orElseThrow(() -> new NotFoundException("Grupo", dto.getRequestedGroupId()));

        Request request = Request.builder()
                .requestNumber("REQ-" + System.currentTimeMillis())
                .type(dto.getType())
                .status(RequestStatus.PENDING)
                .student(student)
                .requestedGroup(requestedGroup)
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .build();

        request.calculatePriority();
        Request savedRequest = requestRepository.save(request);
        notificationService.notifyRequestStatusChange(savedRequest.getId(), "PENDING");

        return requestMapper.toRequestResponse(savedRequest);
    }

    @Override
    public RequestResponse getRequestById(String id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Solicitud", id));
        return requestMapper.toRequestResponse(request);
    }

    @Override
    public RequestResponse getRequestByNumber(String requestNumber) {
        Request request = requestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestNumber));
        return requestMapper.toRequestResponse(request);
    }

    @Override
    public List<RequestResponse> getRequestsByStudent(String studentId) {
        // Validar que el usuario autenticado sea el estudiante o tenga permisos
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName()) &&
                !studentId.equals(getStudentIdFromUser(currentUser))) {
            throw new ForbiddenException("No tienes permiso para ver estas solicitudes");
        }

        List<Request> requests = requestRepository.findByStudentId(studentId);
        return requests.stream()
                .map(requestMapper::toRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponse> getRequestsByStatus(RequestStatus status) {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo los administradores pueden ver solicitudes por estado global");
        }

        List<Request> requests = requestRepository.findByStatus(status);
        return requests.stream()
                .map(requestMapper::toRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponse> getAllRequests() {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo los administradores pueden ver todas las solicitudes");
        }

        List<Request> requests = requestRepository.findAll();
        return requests.stream()
                .map(requestMapper::toRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestResponse updateRequest(String requestId, RequestUpdateRequest requestUpdateRequest) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));

        if (!RequestStatus.ADDITIONAL_INFO.equals(request.getStatus())) {
            throw new BusinessValidationException("Solo se puede actualizar solicitudes que requieren información adicional");
        }

        // Validar que el usuario sea el estudiante
        User currentUser = getCurrentAuthenticatedUser();
        if (!request.getStudent().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Solo el estudiante puede actualizar su solicitud");
        }

        request.setObservations(requestUpdateRequest.getObservations());
        request.setStatus(RequestStatus.PENDING);
        request.setUpdatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);
        notificationService.notifyRequestStatusChange(requestId, "PENDING");

        return requestMapper.toRequestResponse(savedRequest);
    }

    @Override
    @Transactional
    public void cancelRequest(String requestId, String studentId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));

        if (!request.getStudent().getId().equals(studentId)) {
            throw new ForbiddenException("Solo el estudiante puede cancelar su solicitud");
        }

        if (!RequestStatus.PENDING.equals(request.getStatus()) &&
                !RequestStatus.ADDITIONAL_INFO.equals(request.getStatus())) {
            throw new BusinessValidationException("No se puede cancelar una solicitud ya procesada");
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(request);
        notificationService.notifyRequestStatusChange(requestId, "CANCELLED");
    }

    @Override
    public List<RequestResponse> getRequestsByFaculty(String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);

        List<Request> requests = requestRepository.findByFacultyId(facultyId);
        return requests.stream()
                .map(requestMapper::toRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponse> getRequestsByFacultyAndStatus(String facultyId, RequestStatus status) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);

        List<Request> requests = requestRepository.findByFacultyIdAndStatus(facultyId, status);
        return requests.stream()
                .map(requestMapper::toRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestBasicResponse> getFacultyRequestsByPriority(String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);

        List<Request> requests = requestRepository.findByFacultyIdAndStatusOrderByCreatedAtAsc(facultyId, RequestStatus.PENDING);
        return requests.stream()
                .map(requestMapper::toRequestBasicResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestBasicResponse> getFacultyRequestsByArrival(String facultyId) {
        return getFacultyRequestsByPriority(facultyId);
    }

    @Override
    @Transactional
    public RequestResponse approveRequest(String requestId, RequestDecisionRequest decision, String processedBy) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));
        User currentUser = userRepository.findById(processedBy)
                .orElseThrow(() -> new NotFoundException("Usuario", processedBy));

        validateApproverAccess(request, currentUser);

        if (!groupService.hasAvailableSpots(request.getRequestedGroup().getId())) {
            throw new BusinessValidationException("No hay cupos disponibles en el grupo");
        }

        RequestStatus previousStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setProcessedBy(currentUser);
        request.setProcessedAt(LocalDateTime.now());
        request.setJustification(decision.getJustification());

        groupService.enrollStudentInGroup(request.getRequestedGroup().getId(), request.getStudent().getId());
        Request savedRequest = requestRepository.save(request);
        saveRequestHistory(request, previousStatus, RequestStatus.APPROVED, currentUser, "Aprobada");
        notificationService.notifyRequestStatusChange(requestId, "APPROVED");

        return requestMapper.toRequestResponse(savedRequest);
    }

    @Override
    @Transactional
    public RequestResponse rejectRequest(String requestId, RequestDecisionRequest decision, String processedBy) {
        if (decision.getJustification() == null || decision.getJustification().trim().isEmpty()) {
            throw new BusinessValidationException("La justificación es obligatoria para rechazar");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));
        User currentUser = userRepository.findById(processedBy)
                .orElseThrow(() -> new NotFoundException("Usuario", processedBy));

        validateApproverAccess(request, currentUser);

        RequestStatus previousStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setProcessedBy(currentUser);
        request.setProcessedAt(LocalDateTime.now());
        request.setJustification(decision.getJustification());

        Request savedRequest = requestRepository.save(request);
        saveRequestHistory(request, previousStatus, RequestStatus.REJECTED, currentUser, "Rechazada");
        notificationService.notifyRequestStatusChange(requestId, "REJECTED");

        return requestMapper.toRequestResponse(savedRequest);
    }

    @Override
    @Transactional
    public RequestResponse requestAdditionalInfo(String requestId, RequestDecisionRequest decision, String processedBy) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));
        User currentUser = userRepository.findById(processedBy)
                .orElseThrow(() -> new NotFoundException("Usuario", processedBy));

        validateApproverAccess(request, currentUser);

        RequestStatus previousStatus = request.getStatus();
        request.setStatus(RequestStatus.ADDITIONAL_INFO);
        request.setProcessedBy(currentUser);
        request.setProcessedAt(LocalDateTime.now());
        request.setJustification(decision.getComments());

        Request savedRequest = requestRepository.save(request);
        saveRequestHistory(request, previousStatus, RequestStatus.ADDITIONAL_INFO, currentUser, "Información adicional solicitada");
        notificationService.notifyRequestStatusChange(requestId, "ADDITIONAL_INFO");

        return requestMapper.toRequestResponse(savedRequest);
    }

    @Override
    @Transactional
    public RequestResponse processSpecialApproval(String requestId, RequestDecisionRequest decision, String processedBy) {
        User currentUser = userRepository.findById(processedBy)
                .orElseThrow(() -> new NotFoundException("Usuario", processedBy));

        if (!"ADMIN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo administradores pueden hacer aprobaciones especiales");
        }

        if (decision.getSpecialApprovalJustification() == null || decision.getSpecialApprovalJustification().trim().isEmpty()) {
            throw new BusinessValidationException("La justificación es obligatoria para aprobación especial");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));

        RequestStatus previousStatus = request.getStatus();
        request.setStatus(RequestStatus.SPECIAL_APPROVAL);
        request.setSpecialApproval(true);
        request.setSpecialApprovalJustification(decision.getSpecialApprovalJustification());
        request.setProcessedBy(currentUser);
        request.setProcessedAt(LocalDateTime.now());

        groupService.enrollStudentInGroup(request.getRequestedGroup().getId(), request.getStudent().getId());
        Request savedRequest = requestRepository.save(request);
        saveRequestHistory(request, previousStatus, RequestStatus.SPECIAL_APPROVAL, currentUser, "Aprobación especial");
        notificationService.notifyRequestStatusChange(requestId, "SPECIAL_APPROVAL");

        return requestMapper.toRequestResponse(savedRequest);
    }

    @Override
    public List<RequestResponse> getPendingRequestsByFaculty(String facultyId) {
        return getRequestsByFacultyAndStatus(facultyId, RequestStatus.PENDING);
    }

    @Override
    public List<RequestBasicResponse> getGlobalRequestsByPriority() {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo los administradores pueden ver solicitudes globales");
        }

        List<Request> requests = requestRepository.findByStatusOrderByCreatedAtAsc(RequestStatus.PENDING);
        return requests.stream()
                .map(requestMapper::toRequestBasicResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestHistoryResponse> getRequestHistory(String requestId) {
        // Validar acceso a la solicitud
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Solicitud", requestId));
        User currentUser = getCurrentAuthenticatedUser();

        String userRole = currentUser.getRole().getName();
        if (!"ADMIN".equals(userRole)) {
            if ("DEAN".equals(userRole)) {
                Dean dean = deanRepository.findByUser(currentUser)
                        .orElseThrow(() -> new NotFoundException("Decano", currentUser.getId()));
                if (!dean.getFaculty().getId().equals(request.getStudent().getProgram().getFaculty().getId())) {
                    throw new ForbiddenException("No tienes permiso para ver el historial de esta solicitud");
                }
            } else if (!request.getStudent().getUser().getId().equals(currentUser.getId())) {
                throw new ForbiddenException("No tienes permiso para ver el historial de esta solicitud");
            }
        }

        List<RequestHistory> histories = requestHistoryRepository.findByRequestIdOrderByChangedAtDesc(requestId);
        return histories.stream()
                .map(requestMapper::toRequestHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countRequestsByStatusAndFaculty(RequestStatus status, String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);
        return requestRepository.countByStudentProgramFacultyAndStatus(facultyId, status);
    }

    @Override
    public RequestStatsResponse getRequestStatistics(String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);

        RequestStatsResponse stats = new RequestStatsResponse();
        stats.setFacultyId(facultyId);

        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new NotFoundException("Facultad", facultyId));
        stats.setFacultyName(faculty.getName());

        long total = requestRepository.countByStudentProgramFaculty(facultyId);
        long pending = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.PENDING);
        long approved = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.APPROVED);
        long rejected = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.REJECTED);
        long additionalInfo = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.ADDITIONAL_INFO);
        long cancelled = requestRepository.countByStudentProgramFacultyAndStatus(facultyId, RequestStatus.CANCELLED);

        stats.setTotalRequests((int) total);
        stats.setPendingRequests((int) pending);
        stats.setApprovedRequests((int) approved);
        stats.setRejectedRequests((int) rejected);
        stats.setAdditionalInfoRequests((int) additionalInfo);
        stats.setCancelledRequests((int) cancelled);

        long processed = approved + rejected;
        if (processed > 0) {
            stats.setApprovalRate(Math.round((approved * 100.0) / processed * 100.0) / 100.0);
            stats.setRejectionRate(Math.round((rejected * 100.0) / processed * 100.0) / 100.0);
        } else {
            stats.setApprovalRate(0.0);
            stats.setRejectionRate(0.0);
        }

        List<Request> processedRequests = requestRepository.findByFacultyIdAndStatusIn(
                facultyId,
                List.of(RequestStatus.APPROVED, RequestStatus.REJECTED)
        );

        if (!processedRequests.isEmpty()) {
            double totalHours = processedRequests.stream()
                    .filter(r -> r.getProcessedAt() != null)
                    .mapToLong(r -> java.time.Duration.between(r.getCreatedAt(), r.getProcessedAt()).toHours())
                    .average()
                    .orElse(0.0);
            stats.setAverageProcessingTime(totalHours);
        } else {
            stats.setAverageProcessingTime(0.0);
        }

        stats.setCalculatedAt(LocalDateTime.now());
        return stats;
    }

    @Override
    public boolean validateRequest(RequestCreateRequest requestCreateRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"STUDENT".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo los estudiantes pueden crear solicitudes");
        }

        Student student = studentRepository.findById(String.valueOf(requestCreateRequest.getStudentId()))
                .orElseThrow(() -> new NotFoundException("Estudiante", String.valueOf(requestCreateRequest.getStudentId())));

        if (!student.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("No puedes crear solicitudes para otro estudiante");
        }

        String facultyId = student.getProgram().getFaculty().getId();
        if (!academicService.isRequestPeriodActive(facultyId)) {
            throw new BusinessValidationException("El período de solicitudes no está activo para tu facultad");
        }

        if (requestCreateRequest.getType() == null) {
            throw new BusinessValidationException("El tipo de solicitud es requerido");
        }

        switch (requestCreateRequest.getType()) {
            case CHANGE_GROUP:
                validateGroupChangeRequest(requestCreateRequest, student);
                break;
            case CHANGE_SUBJECT:
                validateSubjectChangeRequest(requestCreateRequest, student);
                break;
            case STUDY_PLAN_CHANGE:
                if (requestCreateRequest.getRequestedSubjectId() == null) {
                    throw new BusinessValidationException("Se requiere una materia para el cambio de plan de estudio");
                }
                break;
            default:
                throw new BusinessValidationException("Tipo de solicitud no soportado");
        }

        return true;
    }

    @Override
    public boolean hasScheduleConflict(String studentId, String groupId) {
        return groupService.hasScheduleConflict(studentId, groupId);
    }

    @Override
    public boolean meetsAcademicRequirements(String studentId, String subjectId) {
        return academicService.validatePrerequisites(studentId, subjectId);
    }

    // === MÉTODOS AUXILIARES ===

    private void validateGroupChangeRequest(RequestCreateRequest request, Student student) {
        if (request.getCurrentGroupId() == null) {
            throw new BusinessValidationException("Se requiere el grupo actual");
        }

        Group currentGroup = groupRepository.findById(request.getCurrentGroupId())
                .orElseThrow(() -> new NotFoundException("Grupo actual", request.getCurrentGroupId()));

        if (!isStudentEnrolledInGroup(student.getId(), currentGroup.getId())) {
            throw new BusinessValidationException("No estás inscrito en el grupo actual especificado");
        }

        if (request.getRequestedGroupId() == null) {
            throw new BusinessValidationException("Se requiere el grupo solicitado");
        }

        Group requestedGroup = groupRepository.findById(request.getRequestedGroupId())
                .orElseThrow(() -> new NotFoundException("Grupo solicitado", request.getRequestedGroupId()));

        if (!currentGroup.getSubject().getId().equals(requestedGroup.getSubject().getId())) {
            throw new BusinessValidationException("El grupo solicitado debe ser de la misma materia");
        }

        if (!requestedGroup.hasAvailableSpots()) {
            throw new BusinessValidationException("El grupo solicitado no tiene cupos disponibles");
        }

        if (groupService.hasScheduleConflict(student.getId(), requestedGroup.getId())) {
            throw new BusinessValidationException("El horario del grupo solicitado se superpone con tus materias actuales");
        }
    }

    private void validateSubjectChangeRequest(RequestCreateRequest request, Student student) {
        if (request.getRequestedSubjectId() == null) {
            throw new BusinessValidationException("Se requiere la materia solicitada");
        }

        Subject requestedSubject = subjectRepository.findById(request.getRequestedSubjectId())
                .orElseThrow(() -> new NotFoundException("Materia solicitada", request.getRequestedSubjectId()));

        // Verificar si ya aprobó la materia
        if (studentService.hasApprovedSubject(student.getId(), requestedSubject.getId())) {
            throw new BusinessValidationException("Ya aprobaste esta materia");
        }

        // Verificar si puede recursar (si está reprobada)
        if (studentService.hasFailedSubject(student.getId(), requestedSubject.getId())) {
            if (!studentService.canRetakeSubject(student.getId(), requestedSubject.getId())) {
                throw new BusinessValidationException("No puedes recursar esta materia (máximo de intentos alcanzado)");
            }
        }

        // Validar prerrequisitos
        if (!academicService.validatePrerequisites(student.getId(), requestedSubject.getId())) {
            throw new BusinessValidationException("No cumples con los prerrequisitos de la materia solicitada");
        }
    }

    private boolean isStudentEnrolledInGroup(String studentId, String groupId) {
        List<StudentSchedule> schedules = studentScheduleRepository.findByStudentId(studentId);
        return schedules.stream()
                .flatMap(s -> s.getEnrolledGroups().stream())
                .anyMatch(g -> g.getId().equals(groupId));
    }

    private boolean isStudentEnrolledInSubject(String studentId, String subjectId) {
        List<StudentSchedule> schedules = studentScheduleRepository.findByStudentId(studentId);
        return schedules.stream()
                .flatMap(s -> s.getEnrolledGroups().stream())
                .anyMatch(g -> g.getSubject().getId().equals(subjectId));
    }

    private void validateApproverAccess(Request request, User currentUser) {
        String userRole = currentUser.getRole().getName();
        String studentFacultyId = request.getStudent().getProgram().getFaculty().getId();

        if ("ADMIN".equals(userRole)) return;

        if ("DEAN".equals(userRole)) {
            Dean dean = deanRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Decano", currentUser.getId()));
            if (!dean.getFaculty().getId().equals(studentFacultyId)) {
                throw new ForbiddenException("No puedes gestionar solicitudes de otra facultad");
            }
            return;
        }

        throw new ForbiddenException("Solo decanos o administradores pueden aprobar solicitudes");
    }

    private void saveRequestHistory(Request request, RequestStatus prevStatus, RequestStatus newStatus, User user, String comment) {
        RequestHistory history = RequestHistory.builder()
                .request(request)
                .previousStatus(prevStatus)
                .newStatus(newStatus)
                .changedBy(user)
                .changedAt(LocalDateTime.now())
                .comments(comment)
                .build();
        requestHistoryRepository.save(history);
    }

    private User getCurrentAuthenticatedUser() {
        return getUser(userRepository);
    }

    private void validateFacultyAccess(String facultyId, User user) {
        String userRole = user.getRole().getName();

        if ("ADMIN".equals(userRole)) return;

        if ("DEAN".equals(userRole)) {
            Dean dean = deanRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Decano", user.getId()));
            if (!dean.getFaculty().getId().equals(facultyId)) {
                throw new ForbiddenException("No puedes gestionar solicitudes de otra facultad");
            }
            return;
        }

        if ("STUDENT".equals(userRole)) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Estudiante", user.getId()));
            if (!student.getProgram().getFaculty().getId().equals(facultyId)) {
                throw new ForbiddenException("No tienes permiso para ver estadísticas de otra facultad");
            }
            return;
        }

        throw new ForbiddenException("No tienes permiso para esta operación");
    }

    private String getStudentIdFromUser(User user) {
        if ("STUDENT".equals(user.getRole().getName())) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Estudiante", user.getId()));
            return student.getId();
        }
        return null;
    }
}