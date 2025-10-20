package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.Professor;
import eci.edu.dosw.proyecto.model.Subject;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.repository.ProfessorRepository;
import eci.edu.dosw.proyecto.repository.SubjectRepository;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import eci.edu.dosw.proyecto.utils.GroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final GroupMapper groupMapper;

    // --------------------------
    // CREACIÓN DE GRUPOS
    // --------------------------
    @Override
    public Group createGroup(Group group) {
        if (group.getSubjectId() == null ||
                subjectRepository.findById(group.getSubjectId()).isEmpty()) {
            throw new BusinessException("Debe asociar una materia existente al grupo.");
        }

        if (groupRepository.findByGroupCode(group.getGroupCode()).isPresent()) {
            throw new BusinessException("Código de grupo ya existente.");
        }

        if (group.getMaxCapacity() == null || group.getMaxCapacity() < 1) {
            throw new BusinessException("El cupo máximo debe ser mayor o igual a 1.");
        }

        group.setCurrentEnrollment(0);
        group.setWaitingListCount(0);
        group.setActive(true);

        return groupRepository.save(group);
    }

    // --------------------------
    // CAPACIDAD DEL GRUPO
    // --------------------------
    @Override
    public GroupCapacityResponseDTO getCapacity(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        return groupMapper.toCapacityDTO(group);
    }

    // --------------------------
    // CONSULTAS
    // --------------------------
    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public Group getGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
    }

    @Override
    public List<Group> getGroupsBySubject(String subjectId) {
        return groupRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Group> getGroupsByFaculty(String faculty) {
        return groupRepository.findByFaculty(Faculty.valueOf(faculty));
    }

    @Override
    public List<Group> getAvailableGroups(String subjectId) {
        return groupRepository.findBySubjectId(subjectId).stream()
                .filter(group -> group.getActive() &&
                        group.getCurrentEnrollment() < group.getMaxCapacity())
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsWithAvailability() {
        return groupRepository.findAll().stream()
                .filter(group -> group.getActive() &&
                        group.getCurrentEnrollment() < group.getMaxCapacity())
                .collect(Collectors.toList());
    }

    // --------------------------
    // ACTUALIZAR CAPACIDAD
    // --------------------------
    @Override
    public Group updateGroupCapacity(String groupId, Integer newCapacity) {
        Group group = getGroupById(groupId);

        if (newCapacity < group.getCurrentEnrollment()) {
            throw new RuntimeException(
                    "New capacity cannot be less than current enrollment: " + group.getCurrentEnrollment());
        }

        group.setMaxCapacity(newCapacity);
        return groupRepository.save(group);
    }

    // --------------------------
    // ASIGNAR PROFESOR A GRUPO
    // --------------------------
    @Override
    public Group assignProfessorToGroup(String groupId, String professorId, String role) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("El grupo no existe."));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new BusinessException("El profesor especificado no existe."));

        // Validación de roles autorizados
        if (!role.equalsIgnoreCase("DECANO") && !role.equalsIgnoreCase("ADMIN")) {
            throw new BusinessException("Acción prohibida: el rol no tiene permisos para asignar o retirar profesores.");
        }

        // Si el grupo ya tiene profesor asignado
        if (group.getProfessor() != null && !group.getProfessor().isEmpty()) {
            throw new BusinessException("El grupo ya tiene un profesor asignado. Confirme reemplazo o cancele la operación.");
        }

        // Asignar el profesor
        group.setProfessor(professor.getName());
        group.setProfessorId(professor.getId());
        group.setProfessorName(professor.getName());

        return groupRepository.save(group);
    }

    // --------------------------
    // REMOVER PROFESOR DE GRUPO
    // --------------------------
    @Override
    public Group removeProfessorFromGroup(String groupId, String role) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("El grupo no existe."));

        if (!role.equalsIgnoreCase("DECANO") && !role.equalsIgnoreCase("ADMIN")) {
            throw new BusinessException("Acción prohibida: el rol no tiene permisos para asignar o retirar profesores.");
        }

        if (group.getProfessor() == null || group.getProfessor().isEmpty()) {
            throw new BusinessException("El grupo no tiene profesor asignado para retirar.");
        }

        group.setProfessor(null);
        group.setProfessorId(null);
        group.setProfessorName(null);

        return groupRepository.save(group);
    }
    @Override
    public GroupCapacityResponseDTO getGroupCapacity(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("El grupo no existe."));

        double percentageUsed = (group.getMaxCapacity() == 0)
                ? 0.0
                : ((double) group.getCurrentEnrollment() / group.getMaxCapacity()) * 100;

        return new GroupCapacityResponseDTO(
                group.getCurrentEnrollment(),
                group.getMaxCapacity(),
                percentageUsed
        );
    }
}
