package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final ChangeRequestService changeRequestService;
    private final ValidationService validationService;

    @Override
    @Transactional
    public EnrollmentResponseDTO enrollStudentToGroup(String studentId, EnrollmentRequestDTO enrollmentDTO) {
        log.info("Procesando inscripción del estudiante {} al grupo {}", studentId, enrollmentDTO.getGroupId());

        // Validar estudiante
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        // Validar grupo
        Group group = groupRepository.findById(enrollmentDTO.getGroupId())
                .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

        // Validar materia
        Subject subject = subjectRepository.findById(group.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Materia no encontrada"));

        // Validar elegibilidad
        ValidationResponseDTO eligibility = validateEnrollmentEligibility(studentId, enrollmentDTO.getGroupId());
        if (!eligibility.isValid()) {
            throw new BusinessException(eligibility.getMessage());
        }

        // Validar período académico
        ValidationResponseDTO periodValidation = changeRequestService.validateAcademicPeriodActive();
        if (!periodValidation.isValid()) {
            throw new BusinessException(periodValidation.getMessage());
        }

        // Validar que no está ya inscrito en esta materia
        boolean alreadyEnrolled = student.getCurrentEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getSubjectId().equals(group.getSubjectId())
                        && enrollment.getStatus() == EnrollmentStatus.ACTIVE);

        if (alreadyEnrolled) {
            throw new BusinessException("Ya estás inscrito en esta materia");
        }

        // Crear la inscripción
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setSubjectId(group.getSubjectId());
        enrollment.setGroupId(enrollmentDTO.getGroupId());
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Actualizar contador del grupo
        group.setCurrentEnrollment(group.getCurrentEnrollment() + 1);
        groupRepository.save(group);

        log.info("Inscripción exitosa: estudiante {} inscrito en grupo {}", studentId, enrollmentDTO.getGroupId());

        return buildEnrollmentResponseDTO(savedEnrollment, subject, group, "Inscripción exitosa");
    }

    @Override
    @Transactional
    public EnrollmentResponseDTO cancelEnrollment(String studentId, EnrollmentRequestDTO enrollmentDTO) {
        log.info("Cancelando inscripción del estudiante {} del grupo {}", studentId, enrollmentDTO.getGroupId());

        // Buscar la inscripción activa
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndGroupIdAndStatus(
                        studentId, enrollmentDTO.getGroupId(), EnrollmentStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Inscripción no encontrada o ya cancelada"));

        // Obtener grupo para actualizar contador
        Group group = groupRepository.findById(enrollmentDTO.getGroupId())
                .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

        // Obtener materia para la respuesta
        Subject subject = subjectRepository.findById(enrollment.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Materia no encontrada"));

        // Cancelar inscripción
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        Enrollment cancelledEnrollment = enrollmentRepository.save(enrollment);

        // Actualizar contador del grupo
        group.setCurrentEnrollment(Math.max(0, group.getCurrentEnrollment() - 1));
        groupRepository.save(group);

        log.info("Inscripción cancelada: estudiante {} canceló grupo {}", studentId, enrollmentDTO.getGroupId());

        return buildEnrollmentResponseDTO(cancelledEnrollment, subject, group, "Inscripción cancelada exitosamente");
    }

    @Override
    public List<EnrollmentResponseDTO> getStudentEnrollments(String studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream()
                .map(enrollment -> {
                    Group group = groupRepository.findById(enrollment.getGroupId()).orElse(null);
                    Subject subject = subjectRepository.findById(enrollment.getSubjectId()).orElse(null);
                    return buildEnrollmentResponseDTO(enrollment, subject, group, null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ValidationResponseDTO validateGroupAvailability(String groupId) {
        try {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

            if (!group.getActive()) {
                return new ValidationResponseDTO(false, "Grupo no disponible", "El grupo no está activo");
            }

            if (group.getCurrentEnrollment() >= group.getMaxCapacity()) {
                return new ValidationResponseDTO(false, "Grupo lleno",
                        String.format("El grupo tiene %d/%d estudiantes", group.getCurrentEnrollment(), group.getMaxCapacity()));
            }

            return new ValidationResponseDTO(true, "Grupo disponible",
                    String.format("Cupos disponibles: %d", group.getMaxCapacity() - group.getCurrentEnrollment()));

        } catch (Exception e) {
            return new ValidationResponseDTO(false, "Error validando grupo", e.getMessage());
        }
    }

    @Override
    public ValidationResponseDTO validateEnrollmentEligibility(String studentId, String groupId) {
        try {
            // Validar estudiante
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

            // Validar grupo
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

            // Validar materia
            Subject subject = subjectRepository.findById(group.getSubjectId())
                    .orElseThrow(() -> new NotFoundException("Materia no encontrada"));

            // Validar estado académico del estudiante
            if (student.getStatus() == AcademicStatus.RED) {
                return new ValidationResponseDTO(false, "No elegible",
                        "El estudiante no está habilitado para inscripciones por estado académico ROJO");
            }

            // Validar que la materia está en el plan académico
            boolean isInAcademicPlan = student.getAcademicPlans().stream()
                    .flatMap(plan -> plan.getAvailableSubjects().stream())
                    .anyMatch(subj -> subj.getId().equals(group.getSubjectId()));

            if (!isInAcademicPlan) {
                return new ValidationResponseDTO(false, "Materia no disponible",
                        "La materia no está en tu plan de estudios");
            }

            // Validar que no está ya inscrito en esta materia
            boolean alreadyEnrolled = student.getCurrentEnrollments().stream()
                    .anyMatch(enrollment -> enrollment.getSubjectId().equals(group.getSubjectId())
                            && enrollment.getStatus() == EnrollmentStatus.ACTIVE);

            if (alreadyEnrolled) {
                return new ValidationResponseDTO(false, "Ya inscrito",
                        "Ya estás inscrito en esta materia");
            }

            // Validar conflicto de horario
            ValidationResponseDTO scheduleValidation = changeRequestService.validateScheduleConflict(studentId, groupId);
            if (!scheduleValidation.isValid()) {
                return scheduleValidation;
            }

            return new ValidationResponseDTO(true, "Elegible para inscripción",
                    "Cumple con todos los requisitos para inscribirse");

        } catch (Exception e) {
            return new ValidationResponseDTO(false, "Error validando elegibilidad", e.getMessage());
        }
    }

    private EnrollmentResponseDTO buildEnrollmentResponseDTO(Enrollment enrollment, Subject subject, Group group, String message) {
        EnrollmentResponseDTO response = new EnrollmentResponseDTO();
        response.setId(enrollment.getId());
        response.setStudentId(enrollment.getStudentId());
        response.setSubjectId(enrollment.getSubjectId());
        response.setGroupId(enrollment.getGroupId());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        response.setStatus(enrollment.getStatus());
        response.setMessage(message);

        if (subject != null) {
            response.setSubjectName(subject.getName());
        }

        if (group != null) {
            response.setGroupCode(group.getGroupCode());
        }

        return response;
    }
}