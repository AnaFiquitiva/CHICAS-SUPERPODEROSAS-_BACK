package eci.edu.dosw.proyecto.service.impl;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final AcademicPlanRepository academicPlanRepository;

    @Override
    public void validateRequest(ChangeRequestRequestDTO requestDTO, String studentId) {
        switch (requestDTO.getType()) {
            case GROUP_CHANGE:
                validateGroupChange(requestDTO, studentId);
                break;
            case SUBJECT_CHANGE:
                validateSubjectChange(requestDTO, studentId);
                break;
            case PLAN_CHANGE:
                validatePlanChange(requestDTO, studentId);
                break;
            case NEW_ENROLLMENT:
                validateNewEnrollment(requestDTO, studentId);
                break;
            default:
                throw new BusinessException("Tipo de solicitud no válido");
        }
    }

    @Override
    public void validateStudentEligibility(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        if (student.getStatus() != AcademicStatus.GREEN && student.getStatus() != AcademicStatus.BLUE) {
            throw new BusinessException("El estudiante no está habilitado para realizar cambios");
        }

        if (student.getCurrentEnrollments() == null || student.getCurrentEnrollments().isEmpty()) {
            throw new BusinessException("El estudiante no tiene inscripciones activas");
        }
    }

    @Override
    public void validateGroupChange(ChangeRequestRequestDTO requestDTO, String studentId) {
        if (requestDTO.getCurrentSubjectId() == null || requestDTO.getTargetGroupId() == null) {
            throw new BusinessException("Para cambio de grupo se requiere materia actual y grupo objetivo");
        }

        // Validar que la materia actual existe
        Subject currentSubject = subjectRepository.findById(requestDTO.getCurrentSubjectId())
                .orElseThrow(() -> new NotFoundException("Materia actual no encontrada"));

        // Validar que el grupo objetivo existe y está activo
        Group targetGroup = groupRepository.findById(requestDTO.getTargetGroupId())
                .orElseThrow(() -> new NotFoundException("Grupo objetivo no encontrado"));

        if (!targetGroup.getActive()) {
            throw new BusinessException("El grupo objetivo no está activo");
        }

        if (targetGroup.getCurrentEnrollment() >= targetGroup.getMaxCapacity()) {
            throw new BusinessException("El grupo objetivo está lleno");
        }

        // Validar que el estudiante está inscrito en la materia actual
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        boolean isEnrolledInSubject = student.getCurrentEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getSubjectId().equals(requestDTO.getCurrentSubjectId())
                        && enrollment.getStatus() == EnrollmentStatus.ACTIVE);

        if (!isEnrolledInSubject) {
            throw new BusinessException("El estudiante no está inscrito en la materia actual");
        }
    }

    @Override
    public void validateSubjectChange(ChangeRequestRequestDTO requestDTO, String studentId) {
        if (requestDTO.getCurrentSubjectId() == null || requestDTO.getTargetSubjectId() == null
                || requestDTO.getTargetGroupId() == null) {
            throw new BusinessException("Para cambio de materia se requiere materia actual, materia objetivo y grupo objetivo");
        }

        // Validaciones similares a GROUP_CHANGE pero con materia diferente
        validateGroupChange(requestDTO, studentId);

        // Validar que la materia objetivo pertenece al plan académico del estudiante
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        Subject targetSubject = subjectRepository.findById(requestDTO.getTargetSubjectId())
                .orElseThrow(() -> new NotFoundException("Materia objetivo no encontrada"));

        boolean isInAcademicPlan = student.getAcademicPlans().stream()
                .flatMap(plan -> plan.getAvailableSubjects().stream())
                .anyMatch(subject -> subject.getId().equals(requestDTO.getTargetSubjectId()));

        if (!isInAcademicPlan) {
            throw new BusinessException("La materia objetivo no pertenece a tu plan de estudios");
        }
    }

    @Override
    public void validatePlanChange(ChangeRequestRequestDTO requestDTO, String studentId) {
        if (requestDTO.getPlanChanges() == null || requestDTO.getPlanChanges().isEmpty()) {
            throw new BusinessException("Para cambio de plan se requiere al menos un cambio");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        // Validar cada cambio en el plan
        for (var change : requestDTO.getPlanChanges()) {
            switch (change.getAction()) {
                case "ADD":
                    validateSubjectAvailability(change.getSubjectId(), student);
                    validateGroupAvailability(change.getGroupId());
                    break;
                case "REMOVE":
                    validateCurrentEnrollment(change.getSubjectId(), studentId);
                    break;
                case "REPLACE":
                    validateCurrentEnrollment(change.getSubjectId(), studentId);
                    validateSubjectAvailability(change.getReplacementSubjectId(), student);
                    validateGroupAvailability(change.getReplacementGroupId());
                    break;
                default:
                    throw new BusinessException("Acción no válida: " + change.getAction());
            }
        }
    }

    @Override
    public void validateNewEnrollment(ChangeRequestRequestDTO requestDTO, String studentId) {
        if (requestDTO.getTargetSubjectId() == null || requestDTO.getTargetGroupId() == null) {
            throw new BusinessException("Para nueva inscripción se requiere materia y grupo objetivo");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        validateSubjectAvailability(requestDTO.getTargetSubjectId(), student);
        validateGroupAvailability(requestDTO.getTargetGroupId());
    }

    private void validateSubjectAvailability(String subjectId, Student student) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NotFoundException("Materia no encontrada"));

        boolean isInAcademicPlan = student.getAcademicPlans().stream()
                .flatMap(plan -> plan.getAvailableSubjects().stream())
                .anyMatch(subj -> subj.getId().equals(subjectId));

        if (!isInAcademicPlan) {
            throw new BusinessException("La materia no está disponible en tu plan de estudios");
        }
    }

    private void validateGroupAvailability(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

        if (!group.getActive()) {
            throw new BusinessException("El grupo no está activo");
        }

        if (group.getCurrentEnrollment() >= group.getMaxCapacity()) {
            throw new BusinessException("El grupo está lleno");
        }
    }

    private void validateCurrentEnrollment(String subjectId, String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));

        boolean isEnrolled = student.getCurrentEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getSubjectId().equals(subjectId)
                        && enrollment.getStatus() == EnrollmentStatus.ACTIVE);

        if (!isEnrolled) {
            throw new BusinessException("No estás inscrito en la materia: " + subjectId);
        }
    }
}