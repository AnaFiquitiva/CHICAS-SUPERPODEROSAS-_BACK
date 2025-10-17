package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import eci.edu.dosw.proyecto.service.interfaces.*;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/change-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;
    private final EnrollmentService enrollmentService;
    private final PermissionService permissionService;
    private final GroupService groupService;



    // ENDPOINTS PARA ESTUDIANTES

    /**
     * Obtener todas las solicitudes del estudiante autenticado
     */
    @GetMapping("/students/{studentId}/my-requests")
    public List<ChangeRequestResponseDTO> getMyRequests(@PathVariable String studentId) {
        log.info("Obteniendo todas las solicitudes del estudiante: {}", studentId);
        return changeRequestService.getStudentRequests(studentId);
    }

    /**
     * Obtener solicitudes del estudiante por estado
     */
    @GetMapping("/students/{studentId}/my-requests/status/{status}")
    public List<ChangeRequestResponseDTO> getMyRequestsByStatus(
            @PathVariable String studentId,
            @PathVariable RequestStatus status) {
        log.info("Obteniendo solicitudes del estudiante {} con estado: {}", studentId, status);
        return changeRequestService.getStudentRequestsByStatus(studentId, status);
    }

    /**
     * Obtener solicitudes activas del estudiante (PENDIENTE y EN REVISIÓN)
     */
    @GetMapping("/students/{studentId}/my-requests/active")
    public List<ChangeRequestResponseDTO> getMyActiveRequests(@PathVariable String studentId) {
        log.info("Obteniendo solicitudes activas del estudiante: {}", studentId);
        return changeRequestService.getActiveRequestsByStudent(studentId);
    }

    /**
     * Obtener solicitudes del estudiante por tipo
     */
    @GetMapping("/students/{studentId}/my-requests/type/{type}")
    public List<ChangeRequestResponseDTO> getMyRequestsByType(
            @PathVariable String studentId,
            @PathVariable RequestType type) {
        log.info("Obteniendo solicitudes del estudiante {} con tipo: {}", studentId, type);
        return changeRequestService.getStudentRequestsByType(studentId, type);
    }

    /**
     * Obtener el detalle de una solicitud específica del estudiante
     */
    @GetMapping("/students/{studentId}/my-requests/{requestId}")
    public ChangeRequestResponseDTO getMyRequestById(
            @PathVariable String studentId,
            @PathVariable String requestId) {
        log.info("Obteniendo solicitud {} del estudiante: {}", requestId, studentId);
        return changeRequestService.getStudentRequestById(studentId, requestId);
    }

    /**
     * Buscar solicitud del estudiante por número de solicitud
     */
    @GetMapping("/students/{studentId}/my-requests/number/{requestNumber}")
    public ChangeRequestResponseDTO getMyRequestByNumber(
            @PathVariable String studentId,
            @PathVariable String requestNumber) {
        log.info("Buscando solicitud {} del estudiante: {}", requestNumber, studentId);
        return changeRequestService.getStudentRequestByNumber(studentId, requestNumber);
    }

    /**
     * Obtener estadísticas personales del estudiante
     */
    @GetMapping("/students/{studentId}/my-stats")
    public StudentRequestStatsDTO getMyRequestStats(@PathVariable String studentId) {
        log.info("Obteniendo estadísticas de solicitudes del estudiante: {}", studentId);
        return changeRequestService.getStudentRequestStats(studentId);
    }

    /**
     * Verificar si el estudiante puede crear nuevas solicitudes
     */
    @GetMapping("/students/{studentId}/can-create")
    public ValidationResponseDTO canCreateNewRequest(@PathVariable String studentId) {
        log.info("Verificando si estudiante {} puede crear nueva solicitud", studentId);
        return changeRequestService.validateMaxRequests(studentId);
    }

    /**
     * Proporcionar información adicional - Para estudiantes
     */
    @PutMapping("/{requestId}/provide-info/students/{studentId}")
    public ChangeRequestResponseDTO provideAdditionalInfo(
            @PathVariable String requestId,
            @PathVariable String studentId,
            @RequestParam String additionalInfo) {

        log.info("Estudiante {} proporcionando información adicional para solicitud {}", studentId, requestId);
        return changeRequestService.provideAdditionalInfo(requestId, studentId, additionalInfo);
    }

    // ENDPOINTS PARA ADMINISTRADORES Y DECANOS - GESTIÓN


    /**
     * Aprobar solicitud - Para administradores/decanos
     */
    @PutMapping("/admin/{requestId}/approve")
    public ChangeRequestResponseDTO approveRequest(
            @PathVariable String requestId,
            @RequestParam String employeeCode,
            @RequestParam(required = false) String comments) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} aprobando solicitud {}", employeeCode, requestId);
        return changeRequestService.approveRequest(requestId, employeeCode, comments);
    }

    /**
     * Rechazar solicitud - Para administradores/decanos
     */
    @PutMapping("/admin/{requestId}/reject")
    public ChangeRequestResponseDTO rejectRequest(
            @PathVariable String requestId,
            @RequestParam String employeeCode,
            @RequestParam String reason,
            @RequestParam(required = false) String comments) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} rechazando solicitud {} - razón: {}", employeeCode, requestId, reason);
        return changeRequestService.rejectRequest(requestId, employeeCode, reason, comments);
    }

    /**
     * Solicitar más información - Para administradores/decanos
     */
    @PutMapping("/admin/{requestId}/request-info")
    public ChangeRequestResponseDTO requestMoreInfo(
            @PathVariable String requestId,
            @RequestParam String employeeCode,
            @RequestParam String informationNeeded,
            @RequestParam(required = false, defaultValue = "5") Integer daysToRespond) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} solicitando más información para solicitud {} - información: {}",
                employeeCode, requestId, informationNeeded);
        return changeRequestService.requestMoreInformation(requestId, employeeCode, informationNeeded, daysToRespond);
    }

    /**
     * Asignar solicitud a un administrador/decano
     */
    @PutMapping("/admin/{requestId}/assign")
    public ChangeRequestResponseDTO assignRequest(
            @PathVariable String requestId,
            @RequestParam String assignedByEmployeeCode,
            @RequestParam String assignToEmployeeCode) {

        validateAdminOrDeanAccess(assignedByEmployeeCode);
        log.info("Administrador/Decano {} asignando solicitud {} a {}",
                assignedByEmployeeCode, requestId, assignToEmployeeCode);
        return changeRequestService.getRequestById(requestId);
    }


    // ENDPOINTS PARA ADMINISTRADORES Y DECANOS - CONSULTA

    /**
     * Obtener todas las solicitudes de un estudiante - Para administradores/decanos
     */
    @GetMapping("/admin/students/{studentId}/history")
    public ResponseEntity<?> getStudentHistoryAdmin(
            @PathVariable String studentId,
            @RequestParam String employeeCode) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} consultando historial de solicitudes del estudiante {}", employeeCode, studentId);

        List<ChangeRequestResponseDTO> requests = changeRequestService.getStudentRequests(studentId);

        if (requests == null || requests.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sin registros disponibles");
            response.put("studentId", studentId);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(requests);
    }

    /**
     * Obtener el historial de decisiones de una solicitud.
     */
    @GetMapping("/admin/{requestId}/decision-history")
    public List<RequestHistoryResponseDTO> getDecisionHistoryAdmin(
            @PathVariable String requestId,
            @RequestParam String employeeCode) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} consultando historial de decisiones de la solicitud {}", employeeCode, requestId);

        ChangeRequestResponseDTO request = changeRequestService.getRequestById(requestId);
        if (request == null) {
            throw new BusinessException("Solicitud no encontrada: " + requestId);
        }

        return request.getHistory() != null ? request.getHistory() : java.util.Collections.emptyList();
    }

    // java
    @GetMapping("/admin/faculty/{facultyId}/requests")
    public List<ChangeRequestResponseDTO> getRequestsByFacultyAdmin(
            @PathVariable String facultyId,
            @RequestParam String employeeCode,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestType type) {

        // Verificar que tenga acceso básico de admin/decano
        validateAdminOrDeanAccess(employeeCode);

        // Verificar específicamente que sea Decano y que pertenezca a la facultad
        if (!permissionService.canApprovePlanChanges(employeeCode) ||
                (permissionService instanceof Object && !permissionService.isDeanOfFaculty(employeeCode, facultyId))) {
            throw new BusinessException("No tiene permisos de Decano para acceder a las solicitudes de esta facultad");
        }

        log.info("Decano {} consultando solicitudes de la facultad {} - filtros: status={}, type={}",
                employeeCode, facultyId, status, type);

        // Delegar al servicio. Se asume que existe un método en el servicio que filtre por facultad.
        return changeRequestService.getRequestsByFaculty(facultyId, status, type);
    }


    /**
     * Obtener todas las solicitudes (con filtros opcionales) - Para administradores/decanos
     */
    @GetMapping("/admin")
    public List<ChangeRequestResponseDTO> getAllRequestsAdmin(
            @RequestParam String employeeCode,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestType type,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String periodName) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} consultando todas las solicitudes - filtros: status={}, type={}, studentId={}, periodName={}",
                employeeCode, status, type, studentId, periodName);
        return changeRequestService.getAllRequests(status, type, studentId, periodName);
    }

    /**
     * Obtener solicitudes por estado - Para administradores/decanos
     */
    @GetMapping("/admin/status/{status}")
    public List<ChangeRequestResponseDTO> getRequestsByStatusAdmin(
            @RequestParam String employeeCode,
            @PathVariable RequestStatus status) {
        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} obteniendo solicitudes por estado: {}", employeeCode, status);
        return changeRequestService.getRequestsByStatus(status);
    }

    /**
     * Obtener solicitudes por tipo - Para administradores/decanos
     */
    @GetMapping("/admin/type/{type}")
    public List<ChangeRequestResponseDTO> getRequestsByTypeAdmin(
            @RequestParam String employeeCode,
            @PathVariable RequestType type) {
        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} obteniendo solicitudes por tipo: {}", employeeCode, type);
        return changeRequestService.getRequestsByType(type);
    }

    /**
     * Obtener solicitudes pendientes - Para administradores/decanos
     */
    @GetMapping("/admin/pending")
    public List<ChangeRequestResponseDTO> getPendingRequestsAdmin(@RequestParam String employeeCode) {
        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} obteniendo solicitudes pendientes", employeeCode);
        return changeRequestService.getRequestsByStatus(RequestStatus.PENDING);
    }

    /**
     * Obtener solicitudes en revisión - Para administradores/decanos
     */
    @GetMapping("/admin/under-review")
    public List<ChangeRequestResponseDTO> getUnderReviewRequestsAdmin(@RequestParam String employeeCode) {
        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} obteniendo solicitudes en revisión", employeeCode);
        return changeRequestService.getRequestsByStatus(RequestStatus.UNDER_REVIEW);
    }

    /**
     * Obtener solicitudes que necesitan información - Para administradores/decanos
     */
    @GetMapping("/admin/needs-info")
    public List<ChangeRequestResponseDTO> getNeedsInfoRequestsAdmin(@RequestParam String employeeCode) {
        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} obteniendo solicitudes que necesitan información", employeeCode);
        return changeRequestService.getRequestsByStatus(RequestStatus.NEEDS_INFO);
    }

    /**
     * Obtener estadísticas generales de solicitudes - Para administradores/decanos
     */
    @GetMapping("/admin/stats")
    public RequestStatsDTO getRequestStatsAdmin(@RequestParam String employeeCode) {
        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} obteniendo estadísticas de solicitudes", employeeCode);
        return changeRequestService.getRequestStats();
    }

    // ENDPOINTS PÚBLICOS (sin verificación de rol)


    /**
     * Obtener todas las solicitudes de un estudiante
     */
    @GetMapping("/students/{studentId}")
    public List<ChangeRequestResponseDTO> getStudentRequests(@PathVariable String studentId) {
        log.info("Obteniendo solicitudes del estudiante: {}", studentId);
        return changeRequestService.getStudentRequests(studentId);
    }

    /**
     * Obtener una solicitud por ID
     */
    @GetMapping("/{requestId}")
    public ChangeRequestResponseDTO getRequestById(@PathVariable String requestId) {
        log.info("Obteniendo solicitud: {}", requestId);
        return changeRequestService.getRequestById(requestId);
    }

    /**
     * Obtener una solicitud por número de solicitud
     */
    @GetMapping("/number/{requestNumber}")
    public ChangeRequestResponseDTO getRequestByNumber(@PathVariable String requestNumber) {
        log.info("Obteniendo solicitud por número: {}", requestNumber);
        return changeRequestService.getRequestByNumber(requestNumber);
    }

    /**
     * Cancelar una solicitud
     */
    @PutMapping("/{requestId}/cancel/students/{studentId}")
    public ChangeRequestResponseDTO cancelRequest(
            @PathVariable String requestId,
            @PathVariable String studentId) {

        log.info("Cancelando solicitud: {} por estudiante: {}", requestId, studentId);
        return changeRequestService.cancelRequest(requestId, studentId);
    }

    /**
     * Obtener todas las solicitudes (con filtros opcionales) - Público
     */
    @GetMapping
    public List<ChangeRequestResponseDTO> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestType type,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String periodName) {

        log.info("Obteniendo todas las solicitudes - filtros: status={}, type={}, studentId={}, periodName={}",
                status, type, studentId, periodName);
        return changeRequestService.getAllRequests(status, type, studentId, periodName);
    }

    /**
     * Obtener solicitudes por estado - Público
     */
    @GetMapping("/status/{status}")
    public List<ChangeRequestResponseDTO> getRequestsByStatus(@PathVariable RequestStatus status) {
        log.info("Obteniendo solicitudes por estado: {}", status);
        return changeRequestService.getRequestsByStatus(status);
    }

    /**
     * Obtener solicitudes por tipo - Público
     */
    @GetMapping("/type/{type}")
    public List<ChangeRequestResponseDTO> getRequestsByType(@PathVariable RequestType type) {
        log.info("Obteniendo solicitudes por tipo: {}", type);
        return changeRequestService.getRequestsByType(type);
    }

    /**
     * Obtener solicitudes por período académico - Público
     */
    @GetMapping("/period/{periodName}")
    public List<ChangeRequestResponseDTO> getRequestsByPeriod(@PathVariable String periodName) {
        log.info("Obteniendo solicitudes por período: {}", periodName);
        return changeRequestService.getRequestsByPeriod(periodName);
    }

    /**
     * Obtener solicitudes pendientes - Público
     */
    @GetMapping("/pending")
    public List<ChangeRequestResponseDTO> getPendingRequests() {
        log.info("Obteniendo solicitudes pendientes");
        return changeRequestService.getRequestsByStatus(RequestStatus.PENDING);
    }

    /**
     * Obtener solicitudes en revisión - Público
     */
    @GetMapping("/under-review")
    public List<ChangeRequestResponseDTO> getUnderReviewRequests() {
        log.info("Obteniendo solicitudes en revisión");
        return changeRequestService.getRequestsByStatus(RequestStatus.UNDER_REVIEW);
    }

    /**
     * Obtener solicitudes que necesitan información - Público
     */
    @GetMapping("/needs-info")
    public List<ChangeRequestResponseDTO> getNeedsInfoRequests() {
        log.info("Obteniendo solicitudes que necesitan información");
        return changeRequestService.getRequestsByStatus(RequestStatus.NEEDS_INFO);
    }

    /**
     * Obtener solicitudes aprobadas - Público
     */
    @GetMapping("/approved")
    public List<ChangeRequestResponseDTO> getApprovedRequests() {
        log.info("Obteniendo solicitudes aprobadas");
        return changeRequestService.getRequestsByStatus(RequestStatus.APPROVED);
    }

    /**
     * Obtener solicitudes rechazadas - Público
     */
    @GetMapping("/rejected")
    public List<ChangeRequestResponseDTO> getRejectedRequests() {
        log.info("Obteniendo solicitudes rechazadas");
        return changeRequestService.getRequestsByStatus(RequestStatus.REJECTED);
    }

    /**
     * Obtener estadísticas generales de solicitudes - Público
     */
    @GetMapping("/stats")
    public RequestStatsDTO getRequestStats() {
        log.info("Obteniendo estadísticas de solicitudes");
        return changeRequestService.getRequestStats();
    }

    /**
     * Obtener estadísticas detalladas con filtros - Público
     */
    @GetMapping("/stats/detailed")
    public RequestStatsDTO getDetailedStats(
            @RequestParam(required = false) String periodName,
            @RequestParam(required = false) RequestType type,
            @RequestParam(required = false) String program) {

        log.info("Obteniendo estadísticas detalladas - periodName={}, type={}, program={}",
                periodName, type, program);
        return changeRequestService.getDetailedStats(periodName, type, program);
    }

    /**
     * Buscar solicitud por número de solicitud - Público
     */
    @GetMapping("/search/number/{requestNumber}")
    public ChangeRequestResponseDTO searchRequestByNumber(@PathVariable String requestNumber) {
        log.info("Buscando solicitud por número: {}", requestNumber);
        return changeRequestService.getRequestByNumber(requestNumber);
    }

    /**
     * Buscar solicitudes por estudiante - Público
     */
    @GetMapping("/search/student/{studentId}")
    public List<ChangeRequestResponseDTO> searchRequestsByStudent(@PathVariable String studentId) {
        log.info("Buscando solicitudes del estudiante: {}", studentId);
        return changeRequestService.getStudentRequests(studentId);
    }

    /**
     * Obtener solicitudes recientes - Público
     */
    @GetMapping("/recent")
    public List<ChangeRequestResponseDTO> getRecentRequests() {
        log.info("Obteniendo solicitudes recientes");
        return changeRequestService.getRecentRequests();
    }

    // ENDPOINTS DE VALIDACIÓN


    /**
     * Validar conflicto de horario
     */
    @GetMapping("/validation/schedule-conflict")
    public ValidationResponseDTO checkScheduleConflict(
            @RequestParam String studentId,
            @RequestParam String targetGroupId) {

        log.info("Validando conflicto de horario para estudiante: {} y grupo: {}", studentId, targetGroupId);
        return changeRequestService.validateScheduleConflict(studentId, targetGroupId);
    }

    /**
     * Validar si el período académico está activo
     */
    @GetMapping("/validation/academic-period")
    public ValidationResponseDTO checkAcademicPeriodActive() {
        log.info("Verificando período académico activo");
        return changeRequestService.validateAcademicPeriodActive();
    }

    /**
     * Validar si el estudiante ha alcanzado el límite de solicitudes
     */
    @GetMapping("/validation/max-requests")
    public ValidationResponseDTO checkMaxRequests(@RequestParam String studentId) {
        log.info("Verificando límite de solicitudes para estudiante: {}", studentId);
        return changeRequestService.validateMaxRequests(studentId);
    }

    /**
     * Validar disponibilidad de grupo
     */
    @GetMapping("/validation/group-availability/{groupId}")
    public ValidationResponseDTO checkGroupAvailability(@PathVariable String groupId) {
        log.info("Validando disponibilidad del grupo: {}", groupId);
        return enrollmentService.validateGroupAvailability(groupId);
    }

    /**
     * Validar si el estudiante puede inscribirse a un grupo
     */
    @GetMapping("/validation/can-enroll")
    public ValidationResponseDTO canEnrollToGroup(
            @RequestParam String studentId,
            @RequestParam String groupId) {

        log.info("Validando si estudiante {} puede inscribirse al grupo {}", studentId, groupId);
        return enrollmentService.validateEnrollmentEligibility(studentId, groupId);
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN

    /**
     * Verificar permisos de administrador/decano
     */
    private void validateAdminOrDeanAccess(String employeeCode) {
        if (!permissionService.canViewAllRequests(employeeCode)) {
            throw new BusinessException("No tiene permisos para acceder a esta funcionalidad");
        }
    }

    /**
     * Eliminar una solicitud físicamente - Para estudiantes (dueños), administradores y decanos
     */
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Map<String, String>> deleteRequest(
            @PathVariable String requestId,
            @RequestParam String userId,
            @RequestParam String userRole) {

        log.info("{} ({}) solicitando eliminación física de solicitud {}", userRole, userId, requestId);

        try {
            changeRequestService.deleteRequest(requestId, userId, userRole);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Solicitud eliminada exitosamente");
            response.put("requestId", requestId);
            response.put("deletedBy", userId);
            response.put("userRole", userRole);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);

        } catch (BusinessException e) {
            log.error("Error eliminando solicitud {}: {}", requestId, e.getMessage());
            throw e;
        }
    }

    /**
     * Eliminar solicitud por estudiante (endpoint específico para estudiantes)
     */
    @DeleteMapping("/students/{studentId}/requests/{requestId}")
    public ResponseEntity<Map<String, String>> deleteStudentRequest(
            @PathVariable String studentId,
            @PathVariable String requestId) {

        log.info("Estudiante {} solicitando eliminación de su solicitud {}", studentId, requestId);

        changeRequestService.deleteRequest(requestId, studentId, "STUDENT");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Tu solicitud ha sido eliminada exitosamente");
        response.put("requestId", requestId);
        response.put("studentId", studentId);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar solicitud por administrador/decano (endpoint específico)
     */
    @DeleteMapping("/admin/{requestId}")
    public ResponseEntity<Map<String, String>> deleteRequestAdmin(
            @PathVariable String requestId,
            @RequestParam String employeeCode) {

        validateAdminOrDeanAccess(employeeCode);
        log.info("Administrador/Decano {} eliminando solicitud {}", employeeCode, requestId);

        // Determinar el rol basado en los permisos
        String userRole = determineUserRole(employeeCode);

        changeRequestService.deleteRequest(requestId, employeeCode, userRole);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Solicitud eliminada administrativamente");
        response.put("requestId", requestId);
        response.put("deletedBy", employeeCode);
        response.put("userRole", userRole);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    // Método auxiliar para determinar el rol
    private String determineUserRole(String employeeCode) {
        // Lógica simple para determinar si es ADMIN o DEAN
        if (permissionService.canApprovePlanChanges(employeeCode)) {
            return "DEAN";
        } else if (permissionService.canViewAllRequests(employeeCode)) {
            return "ADMIN";
        } else {
            throw new BusinessException("Usuario no tiene permisos suficientes");
        }
    }
    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Group>> getGroupsBySubject(@PathVariable String subjectId) {
        return ResponseEntity.ok(groupService.getGroupsBySubject(subjectId));
    }
    @GetMapping("/faculty/{faculty}")
    public ResponseEntity<List<Group>> getGroupsByFaculty(@PathVariable String faculty) {
        return ResponseEntity.ok(groupService.getGroupsByFaculty(faculty));
    }

    @GetMapping("/available/{subjectId}")
    public ResponseEntity<List<Group>> getAvailableGroups(@PathVariable String subjectId) {
        return ResponseEntity.ok(groupService.getAvailableGroups(subjectId));
    }
}