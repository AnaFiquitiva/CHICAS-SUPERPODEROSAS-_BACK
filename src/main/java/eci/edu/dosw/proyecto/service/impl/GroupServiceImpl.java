package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.exception.BusinessValidationException;
import eci.edu.dosw.proyecto.exception.ForbiddenException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.AcademicService;
import eci.edu.dosw.proyecto.service.interfaces.AlertService;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import eci.edu.dosw.proyecto.service.interfaces.WaitingListService;
import eci.edu.dosw.proyecto.utils.mappers.GroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final AlertRepository alertRepository;
    private final GroupMapper groupMapper;
    private final SystemConfigServiceImpl systemConfigService;
    private final AlertService alertService;
    private final @Lazy WaitingListService waitingListService;
    private final AcademicService academicService;
    private final UserRepository userRepository;
    private final GroupCapacityHistoryRepository groupCapacityHistoryRepository;


    @Override
    @Transactional
    public GroupResponse createGroup(GroupRequest groupRequest) {
        // Validar código único para la materia
        Subject subject = subjectRepository.findById(groupRequest.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        if (groupRepository.findByGroupCodeAndSubjectAndActiveTrue(groupRequest.getGroupCode(), subject).isPresent()) {
            throw new RuntimeException("Ya existe un grupo con este código para la materia");
        }

        // Validar capacidad máxima
        if (groupRequest.getMaxCapacity() <= 0) {
            throw new RuntimeException("La capacidad máxima debe ser mayor a 0");
        }

        // Crear grupo
        Group group = groupMapper.toGroup(groupRequest);
        group.setSubject(subject);
        group.setCurrentEnrollment(0);
        group.setTotalRequests(0);
        group.setApprovedRequests(0);
        group.setRejectedRequests(0);
        group.setPendingRequests(0);
        group.setActive(true);
        group.setCreatedAt(LocalDateTime.now());

        // Asignar profesor si se especifica
        if (groupRequest.getProfessorId() != null) {
            Professor professor = professorRepository.findById(groupRequest.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
            group.setProfessor(professor);
        }

        Group savedGroup = groupRepository.save(group);
        return groupMapper.toGroupResponse(savedGroup);
    }

    @Override
    public GroupResponse getGroupById(String id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return groupMapper.toGroupResponse(group);
    }

    @Override
    public GroupResponse getGroupByCodeAndSubject(String groupCode, String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Group group = groupRepository.findByGroupCodeAndSubjectAndActiveTrue(groupCode, subject)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return groupMapper.toGroupResponse(group);
    }

    @Override
    public List<GroupResponse> getGroupsBySubject(String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        List<Group> groups = groupRepository.findBySubjectAndActiveTrue(subject);
        return groupMapper.toGroupResponseList(groups);
    }

    @Override
    public List<GroupResponse> getGroupsByFaculty(String facultyId) {
        List<Group> groups = groupRepository.findByFacultyIdAndActiveTrue(facultyId);
        return groupMapper.toGroupResponseList(groups);
    }

    @Override
    public List<GroupResponse> getGroupsByProfessor(String professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        List<Group> groups = groupRepository.findByProfessorAndActiveTrue(professor);
        return groupMapper.toGroupResponseList(groups);
    }

    @Override
    public List<GroupResponse> getAllActiveGroups() {
        List<Group> groups = groupRepository.findByActiveTrue();
        return groupMapper.toGroupResponseList(groups);
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(String groupId, GroupUpdateRequest groupRequest) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        groupMapper.updateGroupFromRequest(groupRequest, group);
        group.setUpdatedAt(LocalDateTime.now());

        // Validar que la nueva capacidad no sea menor que la inscripción actual
        if (groupRequest.getMaxCapacity() != null && groupRequest.getMaxCapacity() < group.getCurrentEnrollment()) {
            throw new RuntimeException("La capacidad máxima no puede ser menor que el número actual de estudiantes inscritos: " +
                    group.getCurrentEnrollment());
        }

        Group savedGroup = groupRepository.save(group);
        return groupMapper.toGroupResponse(savedGroup);
    }

    @Override
    @Transactional
    public GroupResponse updateGroupCapacity(String groupId, Integer newCapacity) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        if (newCapacity <= 0) {
            throw new RuntimeException("La capacidad máxima debe ser mayor a 0");
        }

        if (newCapacity < group.getCurrentEnrollment()) {
            throw new RuntimeException("La capacidad máxima no puede ser menor que el número actual de estudiantes inscritos: " +
                    group.getCurrentEnrollment());
        }

        group.setMaxCapacity(newCapacity);
        group.setUpdatedAt(LocalDateTime.now());

        Group savedGroup = groupRepository.save(group);
        return groupMapper.toGroupResponse(savedGroup);
    }

    @Override
    @Transactional
    public void deactivateGroup(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Validar que no tenga estudiantes inscritos
        if (group.getCurrentEnrollment() > 0) {
            throw new RuntimeException("No se puede desactivar un grupo con estudiantes inscritos");
        }

        group.setActive(false);
        group.setUpdatedAt(LocalDateTime.now());
        groupRepository.save(group);
    }

    // Capacity and occupancy
    @Override
    public GroupCapacityResponse getGroupCapacity(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        return groupMapper.toGroupCapacityResponse(group);
    }

    @Override
    public List<GroupCapacityResponse> getGroupsCapacity(List<String> groupIds) {
        List<Group> groups = groupRepository.findAllById(groupIds);
        return groups.stream()
                .map(groupMapper::toGroupCapacityResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponse> getGroupsWithHighOccupancy(Double threshold) {
        List<Group> groups = groupRepository.findByCurrentEnrollmentGreaterThanEqualAndActiveTrue(
                (int) (threshold * 100) // Convertir porcentaje a número absoluto
        );

        return groupMapper.toGroupResponseList(groups);
    }

    @Override
    public boolean hasAvailableSpots(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        return group.hasAvailableSpots();
    }

    @Override
    public Integer getAvailableSpots(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        return group.getMaxCapacity() - group.getCurrentEnrollment();
    }

    // Schedule management
    @Override
    @Transactional
    public ScheduleResponse addScheduleToGroup(ScheduleRequest scheduleRequest) {
        Group group = groupRepository.findById(scheduleRequest.getGroupId())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Validar que no haya conflictos de horario dentro del mismo grupo
        if (hasScheduleConflictInGroup(group, scheduleRequest)) {
            throw new RuntimeException("El horario se superpone con otro horario del mismo grupo");
        }

        Schedule schedule = groupMapper.toSchedule(scheduleRequest);
        schedule.setGroup(group);
        schedule.setCreatedAt(LocalDateTime.now());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return groupMapper.toScheduleResponse(savedSchedule);
    }

    @Override
    public List<ScheduleResponse> getGroupSchedules(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<Schedule> schedules = scheduleRepository.findByGroupAndGroupActiveTrue(group);
        return groupMapper.toScheduleResponseList(schedules);
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(String scheduleId, ScheduleRequest scheduleRequest) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Group group = schedule.getGroup();

        // Validar conflictos excluyendo el horario actual
        if (hasScheduleConflictInGroup(group, scheduleRequest, scheduleId)) {
            throw new RuntimeException("El horario se superpone con otro horario del mismo grupo");
        }

        schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
        schedule.setStartTime(scheduleRequest.getStartTime());
        schedule.setEndTime(scheduleRequest.getEndTime());
        schedule.setClassroom(scheduleRequest.getClassroom());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return groupMapper.toScheduleResponse(savedSchedule);
    }

    @Override
    @Transactional
    public void removeSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        scheduleRepository.delete(schedule);
    }

    @Override
    @Transactional
    public void removeAllGroupSchedules(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<Schedule> schedules = scheduleRepository.findByGroupAndGroupActiveTrue(group);
        scheduleRepository.deleteAll(schedules);
    }

    @Override
    public List<StudentBasicResponse> getGroupEnrolledStudents(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Obtener estudiantes inscritos en el grupo
        List<StudentSchedule> schedules = studentScheduleRepository.findByEnrolledGroupId(groupId);

        return schedules.stream()
                .map(StudentSchedule::getStudent)
                .map(this::mapToStudentBasicResponse)
                .collect(Collectors.toList());
    }

    // Métodos auxiliares
    private boolean hasScheduleConflictInGroup(Group group, ScheduleRequest scheduleRequest) {
        return hasScheduleConflictInGroup(group, scheduleRequest, null);
    }

    private boolean hasScheduleConflictInGroup(Group group, ScheduleRequest scheduleRequest, String excludeScheduleId) {
        List<Schedule> existingSchedules = scheduleRepository.findByGroupAndGroupActiveTrue(group);

        return existingSchedules.stream()
                .filter(schedule -> !schedule.getId().equals(excludeScheduleId))
                .anyMatch(existingSchedule -> hasTimeConflict(
                        scheduleRequest.getDayOfWeek(), scheduleRequest.getStartTime(), scheduleRequest.getEndTime(),
                        existingSchedule.getDayOfWeek(), existingSchedule.getStartTime(), existingSchedule.getEndTime()
                ));
    }

    private boolean hasTimeConflict(String day1, String start1, String end1, String day2, String start2, String end2) {
        if (!day1.equals(day2)) {
            return false;
        }

        int start1Minutes = timeToMinutes(start1);
        int end1Minutes = timeToMinutes(end1);
        int start2Minutes = timeToMinutes(start2);
        int end2Minutes = timeToMinutes(end2);

        return start1Minutes < end2Minutes && end1Minutes > start2Minutes;
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private boolean isStudentEnrolledInGroup(String studentId, String groupId) {
        return studentScheduleRepository.findByEnrolledGroupId(groupId)
                .stream()
                .anyMatch(schedule -> schedule.getStudent().getId().equals(studentId));
    }

    public boolean hasScheduleConflict(String studentId, String groupId) {
        Group requestedGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Obtener horarios del estudiante
        List<Schedule> studentSchedules = getStudentSchedules(studentId);
        List<Schedule> groupSchedules = requestedGroup.getSchedules();

        // Verificar conflictos
        for (Schedule studentSchedule : studentSchedules) {
            for (Schedule groupSchedule : groupSchedules) {
                if (hasTimeConflict(
                        studentSchedule.getDayOfWeek(), studentSchedule.getStartTime(), studentSchedule.getEndTime(),
                        groupSchedule.getDayOfWeek(), groupSchedule.getStartTime(), groupSchedule.getEndTime()
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<Schedule> getStudentSchedules(String studentId) {
        return studentScheduleRepository.findByStudentId(studentId)
                .stream()
                .flatMap(schedule -> schedule.getEnrolledGroups().stream())
                .flatMap(group -> group.getSchedules().stream())
                .collect(Collectors.toList());
    }

    private void enrollStudentInGroupInternal(Student student, Group group) {
        // Buscar el horario del estudiante para el período actual
        String currentPeriod = getCurrentAcademicPeriod();
        StudentSchedule studentSchedule = studentScheduleRepository
                .findByStudentIdAndAcademicPeriod(student.getId(), currentPeriod)
                .orElseGet(() -> createNewStudentSchedule(student, currentPeriod));

        // Agregar el grupo al horario del estudiante
        if (!studentSchedule.getEnrolledGroups().contains(group)) {
            studentSchedule.getEnrolledGroups().add(group);
            studentScheduleRepository.save(studentSchedule);
        }
    }

    private void unenrollStudentFromGroupInternal(Student student, Group group) {
        String currentPeriod = getCurrentAcademicPeriod();
        studentScheduleRepository.findByStudentIdAndAcademicPeriod(student.getId(), currentPeriod)
                .ifPresent(schedule -> {
                    schedule.getEnrolledGroups().remove(group);
                    studentScheduleRepository.save(schedule);
                });
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
        return academicService.getCurrentAcademicPeriodName();
    }

    private StudentBasicResponse mapToStudentBasicResponse(Student student) {
        StudentBasicResponse response = new StudentBasicResponse();
        response.setId(student.getId());
        response.setCode(student.getCode());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setInstitutionalEmail(student.getInstitutionalEmail());
        response.setProgramName(student.getProgram().getName());
        response.setCurrentSemester(student.getCurrentSemester());

        if (student.getTrafficLight() != null) {
            response.setTrafficLightColor(student.getTrafficLight().getColor());
        }

        return response;
    }

        @Override
        @Transactional
        public GroupResponse enrollStudentInGroup(String groupId, String studentId) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new NotFoundException("Grupo", groupId));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

            if (!group.hasAvailableSpots()) {
                throw new BusinessValidationException("No hay cupos disponibles en el grupo");
            }

            StudentSchedule studentSchedule = getOrCreateCurrentStudentSchedule(student);
            if (!studentSchedule.getEnrolledGroups().contains(group)) {
                studentSchedule.getEnrolledGroups().add(group);
                studentScheduleRepository.save(studentSchedule);
            }

            group.setCurrentEnrollment(group.getCurrentEnrollment() + 1);
            group.setUpdatedAt(LocalDateTime.now());
            Group updatedGroup = groupRepository.save(group);

            alertService.checkAndCreateGroupCapacityAlert(groupId);

            return groupMapper.toGroupResponse(updatedGroup);
        }

        @Override
        @Transactional
        public GroupResponse unenrollStudentFromGroup(String groupId, String studentId) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new NotFoundException("Grupo", groupId));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Estudiante", studentId));

            StudentSchedule studentSchedule = getOrCreateCurrentStudentSchedule(student);
            studentSchedule.getEnrolledGroups().removeIf(g -> g.getId().equals(groupId));
            studentScheduleRepository.save(studentSchedule);

            int previousEnrollment = group.getCurrentEnrollment();
            group.setCurrentEnrollment(Math.max(0, group.getCurrentEnrollment() - 1));
            group.setUpdatedAt(LocalDateTime.now());
            Group updatedGroup = groupRepository.save(group);

            if (previousEnrollment >= Math.ceil(group.getMaxCapacity() * 0.9) &&
                    !updatedGroup.shouldTrigger90PercentAlert()) {
                alertService.resolveGroupCapacityAlert(groupId);
            }

            if (updatedGroup.hasAvailableSpots()) {
                waitingListService.promoteNextStudent(groupId);
            }

            return groupMapper.toGroupResponse(updatedGroup);
        }
    private StudentSchedule getOrCreateCurrentStudentSchedule(Student student) {
        String currentPeriod = getCurrentAcademicPeriod();
        return studentScheduleRepository.findByStudentIdAndAcademicPeriod(student.getId(), currentPeriod)
                .orElseGet(() -> createNewStudentSchedule(student, currentPeriod));
    }
    @Override
    public List<GroupResponse> getGroupsAtHighOccupancy(Double threshold) {
        // Validar que el umbral esté entre 0 y 100
        if (threshold == null || threshold < 0 || threshold > 100) {
            threshold = 90.0; // Valor por defecto
        }

        // Obtener todos los grupos activos
        List<Group> activeGroups = groupRepository.findByActiveTrue();

        // Filtrar grupos que superen el umbral
        Double finalThreshold = threshold;
        List<Group> highOccupancyGroups = activeGroups.stream()
                .filter(group -> group.getOccupancyPercentage() >= finalThreshold)
                .collect(Collectors.toList());

        return groupMapper.toGroupResponseList(highOccupancyGroups);
    }
    public List<GroupOccupancyResponse> getGroupsAtHighOccupancyWithDetails(Double threshold) {
        if (threshold == null || threshold < 0 || threshold > 100) {
            threshold = 90.0;
        }

        List<Group> activeGroups = groupRepository.findByActiveTrue();
        List<GroupOccupancyResponse> responses = new ArrayList<>();

        for (Group group : activeGroups) {
            if (group.getOccupancyPercentage() >= threshold) {
                GroupOccupancyResponse response = new GroupOccupancyResponse();
                response.setGroupId(group.getId());
                response.setGroupCode(group.getGroupCode());
                response.setSubjectName(group.getSubject().getName());
                response.setFacultyName(group.getSubject().getFaculty().getName());
                response.setMaxCapacity(group.getMaxCapacity());
                response.setCurrentEnrollment(group.getCurrentEnrollment());
                response.setOccupancyPercentage(group.getOccupancyPercentage());
                response.setWaitingListCount((int) waitingListService.getWaitingListSize(group.getId()));
                response.setHasAvailableSpots(group.hasAvailableSpots());
                responses.add(response);
            }
        }

        return responses;
    }
    @Override
    @Transactional
    public GroupResponse updateGroupCapacity(String groupId, GroupCapacityUpdateRequest request) {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo los administradores pueden modificar cupos");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        // Validar que el nuevo cupo no sea menor que los estudiantes inscritos
        if (request.getNewCapacity() < group.getCurrentEnrollment()) {
            throw new BusinessValidationException(
                    "El nuevo cupo (" + request.getNewCapacity() + ") no puede ser menor que los estudiantes inscritos (" +
                            group.getCurrentEnrollment() + ")"
            );
        }

        // Guardar historial de cambio de capacidad
        GroupCapacityHistory history = GroupCapacityHistory.builder()
                .group(group)
                .previousCapacity(group.getMaxCapacity())
                .newCapacity(request.getNewCapacity())
                .changeReason("Modificación manual de cupo")
                .justification(request.getJustification())
                .modifiedBy(currentUser)
                .modifiedAt(LocalDateTime.now())
                .build();
        groupCapacityHistoryRepository.save(history);

        // Actualizar el cupo del grupo
        group.setMaxCapacity(request.getNewCapacity());
        group.setUpdatedAt(LocalDateTime.now());
        Group updatedGroup = groupRepository.save(group);

        // Verificar si se debe activar la lista de espera (si hay cupos disponibles)
        if (updatedGroup.hasAvailableSpots()) {
            waitingListService.promoteNextStudent(groupId);
        }

        return groupMapper.toGroupResponse(updatedGroup);
    }

    private User getCurrentAuthenticatedUser() {
        return getUser(userRepository);
    }

    static User getUser(UserRepository userRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Usuario no autenticado");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario autenticado", username));
    }
    // eci.edu.dosw.proyecto.service.impl.GroupServiceImpl.java
    @Override
    @Transactional
    public GroupResponse assignProfessorToGroup(String groupId, String professorId) {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName()) && !"DEAN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo administradores o decanos pueden asignar profesores");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new NotFoundException("Profesor", professorId));

        // Validar que el profesor pertenezca a la misma facultad
        if (!professor.getFaculty().getId().equals(group.getSubject().getFaculty().getId())) {
            throw new BusinessValidationException("El profesor no pertenece a la facultad de la materia");
        }

        group.setProfessor(professor);
        group.setUpdatedAt(LocalDateTime.now());
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public GroupResponse removeProfessorFromGroup(String groupId) {
        User currentUser = getCurrentAuthenticatedUser();
        if (!"ADMIN".equals(currentUser.getRole().getName()) && !"DEAN".equals(currentUser.getRole().getName())) {
            throw new ForbiddenException("Solo administradores o decanos pueden retirar profesores");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo", groupId));

        group.setProfessor(null);
        group.setUpdatedAt(LocalDateTime.now());
        Group updatedGroup = groupRepository.save(group);
        return groupMapper.toGroupResponse(updatedGroup);
    }
    @Override
    @Transactional
    public ScheduleResponse addSchedule(ScheduleRequest scheduleRequest) {
        Group group = groupRepository.findById(scheduleRequest.getGroupId())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Validar que no haya conflictos de horario dentro del mismo grupo
        if (hasScheduleConflictInGroup(group, scheduleRequest)) {
            throw new RuntimeException("El horario se superpone con otro horario del mismo grupo");
        }

        Schedule schedule = groupMapper.toSchedule(scheduleRequest);
        schedule.setGroup(group);
        schedule.setCreatedAt(LocalDateTime.now());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return groupMapper.toScheduleResponse(savedSchedule);
    }

    @Override
    @Transactional
    public void removeAllSchedules(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<Schedule> schedules = scheduleRepository.findByGroupAndGroupActiveTrue(group);
        scheduleRepository.deleteAll(schedules);
    }


}