package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/change-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChangeRequestController {

    private final ChangeRequestService changeRequestService;
    private final EnrollmentService enrollmentService;

    /**
     * Crear solicitud de cambio de grupo (misma materia)
     * Ejemplo: Cambiar de Matemáticas Básicas grupo 1 a Matemáticas Básicas grupo 2
     */
    @PostMapping("/group-change/students/{studentId}")
    public ChangeRequestResponseDTO createGroupChangeRequest(
            @PathVariable String studentId,
            @Valid @RequestBody ChangeRequestRequestDTO requestDTO) {

        log.info("Creando solicitud de cambio de grupo para estudiante: {}", studentId);
        requestDTO.setType(eci.edu.dosw.proyecto.model.RequestType.GROUP_CHANGE);
        return changeRequestService.createChangeRequest(studentId, requestDTO);
    }

    /**
     * Crear solicitud de cambio de materia
     * Ejemplo: Cambiar de Matemáticas Básicas a Programación
     */
    @PostMapping("/subject-change/students/{studentId}")
    public ChangeRequestResponseDTO createSubjectChangeRequest(
            @PathVariable String studentId,
            @Valid @RequestBody ChangeRequestRequestDTO requestDTO) {

        log.info("Creando solicitud de cambio de materia para estudiante: {}", studentId);
        requestDTO.setType(eci.edu.dosw.proyecto.model.RequestType.SUBJECT_CHANGE);
        return changeRequestService.createChangeRequest(studentId, requestDTO);
    }

    /**
     * Crear solicitud de cambio en el plan de estudio
     * Ejemplo: Cambiar varias materias del plan actual
     */
    @PostMapping("/plan-change/students/{studentId}")
    public ChangeRequestResponseDTO createPlanChangeRequest(
            @PathVariable String studentId,
            @Valid @RequestBody ChangeRequestRequestDTO requestDTO) {

        log.info("Creando solicitud de cambio de plan para estudiante: {}", studentId);
        requestDTO.setType(eci.edu.dosw.proyecto.model.RequestType.PLAN_CHANGE);
        return changeRequestService.createChangeRequest(studentId, requestDTO);
    }

    /**
     * Crear solicitud de nueva inscripción
     * Ejemplo: Inscribir una materia nueva sin tener una materia actual
     */
    @PostMapping("/new-enrollment/students/{studentId}")
    public ChangeRequestResponseDTO createNewEnrollmentRequest(
            @PathVariable String studentId,
            @Valid @RequestBody ChangeRequestRequestDTO requestDTO) {

        log.info("Creando solicitud de nueva inscripción para estudiante: {}", studentId);
        requestDTO.setType(eci.edu.dosw.proyecto.model.RequestType.NEW_ENROLLMENT);
        return changeRequestService.createChangeRequest(studentId, requestDTO);
    }

    /**
     * INSCRIPCIÓN DIRECTA A UN GRUPO
     * Ejemplo: Inscribirse directamente a Matemáticas Básicas grupo 1
     */
    @PostMapping("/enroll/students/{studentId}")
    public EnrollmentResponseDTO enrollToGroup(
            @PathVariable String studentId,
            @Valid @RequestBody EnrollmentRequestDTO enrollmentDTO) {

        log.info("Inscribiendo estudiante {} al grupo {}", studentId, enrollmentDTO.getGroupId());
        return enrollmentService.enrollStudentToGroup(studentId, enrollmentDTO);
    }

    /**
     * CANCELAR INSCRIPCIÓN
     * Ejemplo: Cancelar inscripción a una materia
     */
    @PostMapping("/cancel-enrollment/students/{studentId}")
    public EnrollmentResponseDTO cancelEnrollment(
            @PathVariable String studentId,
            @Valid @RequestBody EnrollmentRequestDTO enrollmentDTO) {

        log.info("Cancelando inscripción del estudiante {} del grupo {}", studentId, enrollmentDTO.getGroupId());
        return enrollmentService.cancelEnrollment(studentId, enrollmentDTO);
    }

    /**
     * OBTENER INSCRIPCIONES DEL ESTUDIANTE
     */
    @GetMapping("/enrollments/students/{studentId}")
    public List<EnrollmentResponseDTO> getStudentEnrollments(@PathVariable String studentId) {
        log.info("Obteniendo inscripciones del estudiante: {}", studentId);
        return enrollmentService.getStudentEnrollments(studentId);
    }

    /**
     * Endpoint genérico para crear cualquier tipo de solicitud
     */
    @PostMapping("/students/{studentId}")
    public ChangeRequestResponseDTO createChangeRequest(
            @PathVariable String studentId,
            @Valid @RequestBody ChangeRequestRequestDTO requestDTO) {

        log.info("Creando solicitud de tipo {} para estudiante: {}", requestDTO.getType(), studentId);
        return changeRequestService.createChangeRequest(studentId, requestDTO);
    }

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
}