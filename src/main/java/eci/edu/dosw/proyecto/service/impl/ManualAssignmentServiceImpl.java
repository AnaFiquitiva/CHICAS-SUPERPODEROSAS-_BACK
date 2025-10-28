package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.ManualAssignmentRequest;
import eci.edu.dosw.proyecto.dto.ManualAssignmentResponse;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import eci.edu.dosw.proyecto.service.interfaces.ManualAssignmentService;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import eci.edu.dosw.proyecto.utils.mappers.ManualAssignmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static eci.edu.dosw.proyecto.service.impl.GroupServiceImpl.getUser;

@Service
@RequiredArgsConstructor
public class ManualAssignmentServiceImpl implements ManualAssignmentService {

    private final ManualAssignmentRepository manualAssignmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final StudyPlanProgressRepository studyPlanProgressRepository;
    private final SubjectProgressRepository subjectProgressRepository;
    private final UserRepository userRepository;
    private final DeanRepository deanRepository;
    private final GroupService groupService;
    private final StudentService studentService;
    private final ManualAssignmentMapper manualAssignmentMapper;

    @Override
    @Transactional
    public ManualAssignmentResponse createManualAssignment(ManualAssignmentRequest assignmentRequest) {
        User currentUser = getCurrentAuthenticatedUser();
        validateAssignmentPermissions(assignmentRequest.getType(), currentUser);

        ManualAssignment assignment = manualAssignmentMapper.toManualAssignment(assignmentRequest);
        assignment.setAssignedBy(currentUser);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setStatus(AssignmentStatus.PENDING);
        assignment.setCreatedAt(LocalDateTime.now());

        Student student = studentRepository.findById(assignmentRequest.getStudentId())
                .orElseThrow(() -> new NotFoundException("Estudiante", assignmentRequest.getStudentId()));
        assignment.setStudent(student);

        if (assignmentRequest.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(assignmentRequest.getSubjectId())
                    .orElseThrow(() -> new NotFoundException("Materia", assignmentRequest.getSubjectId()));
            assignment.setSubject(subject);
        }

        if (assignmentRequest.getGroupId() != null) {
            Group group = groupRepository.findById(assignmentRequest.getGroupId())
                    .orElseThrow(() -> new NotFoundException("Grupo", assignmentRequest.getGroupId()));
            assignment.setGroup(group);
        }

        ManualAssignment savedAssignment = manualAssignmentRepository.save(assignment);
        return manualAssignmentMapper.toManualAssignmentResponse(savedAssignment);
    }

    @Override
    public ManualAssignmentResponse getManualAssignmentById(String id) {
        ManualAssignment assignment = manualAssignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asignación manual", id));
        validateAssignmentAccess(assignment);
        return manualAssignmentMapper.toManualAssignmentResponse(assignment);
    }

    @Override
    public List<ManualAssignmentResponse> getManualAssignmentsByStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Estudiante", studentId));
        List<ManualAssignment> assignments = manualAssignmentRepository.findByStudent(student);
        return assignments.stream()
                .map(manualAssignmentMapper::toManualAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManualAssignmentResponse> getManualAssignmentsByFaculty(String facultyId) {
        User currentUser = getCurrentAuthenticatedUser();
        validateFacultyAccess(facultyId, currentUser);
        List<ManualAssignment> assignments = manualAssignmentRepository.findByFacultyId(facultyId);
        return assignments.stream()
                .map(manualAssignmentMapper::toManualAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ManualAssignmentResponse executeManualAssignment(String assignmentId) {
        ManualAssignment assignment = manualAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Asignación manual", assignmentId));
        validateAssignmentAccess(assignment);

        if (!AssignmentStatus.PENDING.equals(assignment.getStatus())) {
            throw new BusinessValidationException("Solo se pueden ejecutar asignaciones en estado PENDIENTE");
        }

        switch (assignment.getType()) {
            case SUBJECT_ASSIGNMENT:
                assignStudentToSubjectInternal(assignment);
                break;
            case GROUP_ASSIGNMENT:
                assignStudentToGroupInternal(assignment);
                break;
            case SUBJECT_WITHDRAWAL:
                withdrawStudentFromSubjectInternal(assignment);
                break;
            case GROUP_WITHDRAWAL:
                withdrawStudentFromGroupInternal(assignment);
                break;
            default:
                throw new BusinessValidationException("Tipo de asignación no soportado");
        }

        assignment.setStatus(AssignmentStatus.EXECUTED);
        assignment.setExecutedAt(LocalDateTime.now());
        ManualAssignment savedAssignment = manualAssignmentRepository.save(assignment);
        return manualAssignmentMapper.toManualAssignmentResponse(savedAssignment);
    }

    @Override
    @Transactional
    public void cancelManualAssignment(String assignmentId) {
        ManualAssignment assignment = manualAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Asignación manual", assignmentId));
        validateAssignmentAccess(assignment);

        if (!AssignmentStatus.PENDING.equals(assignment.getStatus())) {
            throw new BusinessValidationException("Solo se pueden cancelar asignaciones en estado PENDIENTE");
        }

        assignment.setStatus(AssignmentStatus.CANCELLED);
        manualAssignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public ManualAssignmentResponse assignStudentToSubject(ManualAssignmentRequest assignmentRequest) {
        assignmentRequest.setType(AssignmentType.SUBJECT_ASSIGNMENT);
        ManualAssignmentResponse response = createManualAssignment(assignmentRequest);
        executeManualAssignment(response.getId());
        return response;
    }

    @Override
    @Transactional
    public ManualAssignmentResponse assignStudentToGroup(ManualAssignmentRequest assignmentRequest) {
        assignmentRequest.setType(AssignmentType.GROUP_ASSIGNMENT);
        ManualAssignmentResponse response = createManualAssignment(assignmentRequest);
        executeManualAssignment(response.getId());
        return response;
    }

    @Override
    @Transactional
    public ManualAssignmentResponse withdrawStudentFromSubject(ManualAssignmentRequest assignmentRequest) {
        assignmentRequest.setType(AssignmentType.SUBJECT_WITHDRAWAL);
        ManualAssignmentResponse response = createManualAssignment(assignmentRequest);
        executeManualAssignment(response.getId());
        return response;
    }

    @Override
    @Transactional
    public ManualAssignmentResponse withdrawStudentFromGroup(ManualAssignmentRequest assignmentRequest) {
        assignmentRequest.setType(AssignmentType.GROUP_WITHDRAWAL);
        ManualAssignmentResponse response = createManualAssignment(assignmentRequest);
        executeManualAssignment(response.getId());
        return response;
    }

    @Override
    public ManualAssignmentResponse validateManualAssignment(String assignmentId) {
        ManualAssignment assignment = manualAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Asignación manual", assignmentId));
        validateAssignmentAccess(assignment);

        StringBuilder validationMessages = new StringBuilder();
        boolean valid = true;

        if (assignment.getType() == AssignmentType.SUBJECT_ASSIGNMENT ||
                assignment.getType() == AssignmentType.GROUP_ASSIGNMENT) {

            if (!assignment.isOverridePrerequisites()) {
                if (studentService.meetsAcademicRequirements(assignment.getStudent().getId(),
                        assignment.getSubject().getId())) {
                    validationMessages.append("No cumple prerrequisitos. ");
                    valid = false;
                }
            }
        }

        if (assignment.getType() == AssignmentType.GROUP_ASSIGNMENT) {
            if (!assignment.isOverrideCapacity()) {
                if (!groupService.hasAvailableSpots(assignment.getGroup().getId())) {
                    validationMessages.append("No hay cupos disponibles. ");
                    valid = false;
                }
            }
        }

        assignment.setPrerequisitesValidated(valid);
        assignment.setCapacityValidated(valid);
        assignment.setCreditLimitValidated(valid);
        assignment.setValidationMessages(validationMessages.toString());
        assignment.setStatus(valid ? AssignmentStatus.VALIDATED : AssignmentStatus.FAILED);

        ManualAssignment savedAssignment = manualAssignmentRepository.save(assignment);
        return manualAssignmentMapper.toManualAssignmentResponse(savedAssignment);
    }

    @Override
    public boolean canOverridePrerequisites(String studentId, String subjectId) {
        User currentUser = getCurrentAuthenticatedUser();
        return "ADMIN".equals(currentUser.getRole().getName());
    }

    @Override
    public boolean canOverrideCapacity(String groupId) {
        User currentUser = getCurrentAuthenticatedUser();
        return "ADMIN".equals(currentUser.getRole().getName());
    }

    @Override
    public boolean canOverrideCreditLimit(String studentId) {
        User currentUser = getCurrentAuthenticatedUser();
        return "ADMIN".equals(currentUser.getRole().getName());
    }

    @Override
    public List<ManualAssignmentResponse> getPendingManualAssignments() {
        List<ManualAssignment> assignments = manualAssignmentRepository.findByStatus(AssignmentStatus.PENDING);
        return assignments.stream()
                .map(manualAssignmentMapper::toManualAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManualAssignmentResponse> getManualAssignmentsByStatus(AssignmentStatus status) {
        List<ManualAssignment> assignments = manualAssignmentRepository.findByStatus(status);
        return assignments.stream()
                .map(manualAssignmentMapper::toManualAssignmentResponse)
                .collect(Collectors.toList());
    }


    private void assignStudentToSubjectInternal(ManualAssignment assignment) {
        Student student = assignment.getStudent();
        Subject subject = assignment.getSubject();

        // Validar que el estudiante no esté ya inscrito en la materia
        if (isStudentEnrolledInSubject(student.getId(), subject.getId())) {
            throw new BusinessValidationException("El estudiante ya está inscrito en esta materia");
        }

        // Validar prerrequisitos (a menos que se sobrepasen)
        if (!assignment.isOverridePrerequisites()) {
            if (studentService.meetsAcademicRequirements(student.getId(), subject.getId())) {
                throw new BusinessValidationException("El estudiante no cumple con los prerrequisitos");
            }
        }

        // Obtener o crear el horario del estudiante para el período actual
        String currentPeriod = getCurrentAcademicPeriod();
        StudentSchedule studentSchedule = studentScheduleRepository
                .findByStudentIdAndAcademicPeriod(student.getId(), currentPeriod)
                .orElseGet(() -> createNewStudentSchedule(student, currentPeriod));

        // Actualizar el plan de estudios del estudiante
        updateStudyPlanProgressForSubjectAssignment(student, subject);

        // Si se especificó un grupo, inscribir en el grupo
        if (assignment.getGroup() != null) {
            if (!assignment.isOverrideCapacity() && !assignment.getGroup().hasAvailableSpots()) {
                throw new BusinessValidationException("No hay cupos disponibles en el grupo especificado");
            }
            groupService.enrollStudentInGroup(assignment.getGroup().getId(), student.getId());
        }
    }

    private void withdrawStudentFromSubjectInternal(ManualAssignment assignment) {
        Student student = assignment.getStudent();
        Subject subject = assignment.getSubject();

        // Validar que el estudiante esté inscrito en la materia
        if (!isStudentEnrolledInSubject(student.getId(), subject.getId())) {
            throw new BusinessValidationException("El estudiante no está inscrito en esta materia");
        }

        // Obtener el horario del estudiante
        String currentPeriod = getCurrentAcademicPeriod();
        StudentSchedule studentSchedule = studentScheduleRepository
                .findByStudentIdAndAcademicPeriod(student.getId(), currentPeriod)
                .orElseThrow(() -> new NotFoundException("Horario del estudiante", "período actual"));

        // Retirar de todos los grupos de la materia
        List<Group> groupsToRemove = new ArrayList<>();
        for (Group group : studentSchedule.getEnrolledGroups()) {
            if (group.getSubject().getId().equals(subject.getId())) {
                groupsToRemove.add(group);
            }
        }
        for (Group group : groupsToRemove) {
            groupService.unenrollStudentFromGroup(group.getId(), student.getId());
        }

        // Actualizar el plan de estudios del estudiante
        updateStudyPlanProgressForSubjectWithdrawal(student, subject);
    }

    private void assignStudentToGroupInternal(ManualAssignment assignment) {
        if (!assignment.isOverrideCapacity() && !groupService.hasAvailableSpots(assignment.getGroup().getId())) {
            throw new BusinessValidationException("No hay cupos disponibles en el grupo");
        }
        groupService.enrollStudentInGroup(assignment.getGroup().getId(), assignment.getStudent().getId());
    }

    private void withdrawStudentFromGroupInternal(ManualAssignment assignment) {
        groupService.unenrollStudentFromGroup(assignment.getGroup().getId(), assignment.getStudent().getId());
    }

    // === MÉTODOS AUXILIARES ===

    private boolean isStudentEnrolledInSubject(String studentId, String subjectId) {
        String currentPeriod = getCurrentAcademicPeriod();
        return studentScheduleRepository.findByStudentIdAndAcademicPeriod(studentId, currentPeriod)
                .stream()
                .flatMap(schedule -> schedule.getEnrolledGroups().stream())
                .anyMatch(group -> group.getSubject().getId().equals(subjectId));
    }

    private void updateStudyPlanProgressForSubjectAssignment(Student student, Subject subject) {
        StudyPlanProgress progress = studyPlanProgressRepository.findByStudent(student)
                .orElseGet(() -> createNewStudyPlanProgress(student));

        // Verificar si ya existe progreso para esta materia
        boolean subjectExists = progress.getSubjectProgress().stream()
                .anyMatch(sp -> sp.getSubject().getId().equals(subject.getId()));

        if (!subjectExists) {
            SubjectProgress newProgress = SubjectProgress.builder()
                    .subject(subject)
                    .status("PENDING")
                    .academicPeriod(getCurrentAcademicPeriod())
                    .build();
            progress.getSubjectProgress().add(newProgress);
            studyPlanProgressRepository.save(progress);
        }
    }

    private void updateStudyPlanProgressForSubjectWithdrawal(Student student, Subject subject) {
        StudyPlanProgress progress = studyPlanProgressRepository.findByStudent(student)
                .orElseThrow(() -> new NotFoundException("Progreso del plan de estudios", student.getId()));

        // Eliminar el progreso de la materia
        progress.getSubjectProgress().removeIf(sp -> sp.getSubject().getId().equals(subject.getId()));
        studyPlanProgressRepository.save(progress);
    }

    private StudyPlanProgress createNewStudyPlanProgress(Student student) {
        StudyPlanProgress progress = StudyPlanProgress.builder()
                .student(student)
                .program(student.getProgram())
                .totalCredits(student.getProgram().getTotalCredits())
                .completedCredits(0)
                .progressPercentage(0.0)
                .subjectProgress(new ArrayList<>())
                .calculatedAt(LocalDateTime.now())
                .build();
        return studyPlanProgressRepository.save(progress);
    }

    private StudentSchedule createNewStudentSchedule(Student student, String academicPeriod) {
        StudentSchedule schedule = new StudentSchedule();
        schedule.setStudent(student);
        schedule.setAcademicPeriod(academicPeriod);
        schedule.setAcademicYear(LocalDateTime.now().getYear());
        schedule.setEnrolledGroups(new ArrayList<>());
        schedule.setCreatedAt(LocalDateTime.now());
        return studentScheduleRepository.save(schedule);
    }

    private String getCurrentAcademicPeriod() {
        // TODO: Implementar con AcademicService
        return "2025-1";
    }

    private void validateAssignmentPermissions(AssignmentType type, User user) {
        String role = user.getRole().getName();
        if ("ADMIN".equals(role)) return;
        if ("DEAN".equals(role)) return;
        throw new ForbiddenException("Solo decanos o administradores pueden crear asignaciones manuales");
    }

    private void validateAssignmentAccess(ManualAssignment assignment) {
        User currentUser = getCurrentAuthenticatedUser();
        String role = currentUser.getRole().getName();

        if ("ADMIN".equals(role)) return;

        if ("DEAN".equals(role)) {
            Dean dean = deanRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Decano", currentUser.getId()));
            String studentFacultyId = assignment.getStudent().getProgram().getFaculty().getId();
            if (!dean.getFaculty().getId().equals(studentFacultyId)) {
                throw new ForbiddenException("No puedes gestionar asignaciones de otra facultad");
            }
            return;
        }

        throw new ForbiddenException("No tienes permiso para acceder a esta asignación");
    }

    private void validateFacultyAccess(String facultyId, User user) {
        String role = user.getRole().getName();
        if ("ADMIN".equals(role)) return;
        if ("DEAN".equals(role)) {
            Dean dean = deanRepository.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Decano", user.getId()));
            if (!dean.getFaculty().getId().equals(facultyId)) {
                throw new ForbiddenException("No puedes gestionar asignaciones de otra facultad");
            }
            return;
        }
        throw new ForbiddenException("No tienes permiso para gestionar asignaciones de esta facultad");
    }

    private User getCurrentAuthenticatedUser() {
        return getUser(userRepository);
    }
}