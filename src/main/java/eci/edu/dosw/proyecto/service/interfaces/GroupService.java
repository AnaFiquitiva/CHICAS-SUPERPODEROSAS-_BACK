package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.model.Group;

import java.util.List;

public interface GroupService {
    /**
     * Obtiene el estado del cupo del grupo indicado.
     */
    /**
     * Crea un grupo asociado a una materia existente.
     */
    Group createGroup(Group group);

    /**
     * Obtiene el estado del cupo del grupo indicado.
     */
    GroupCapacityResponseDTO getCapacity(String groupId);
    List<Group> getAllGroups();
    Group getGroupById(String groupId);
    List<Group> getGroupsBySubject(String subjectId);
    List<Group> getGroupsByFaculty(String faculty);
    List<Group> getAvailableGroups(String subjectId);
    List<Group> getGroupsWithAvailability();
    Group updateGroupCapacity(String groupId, Integer newCapacity);
    Group assignProfessorToGroup(String groupId, String professorId, String role);
    Group removeProfessorFromGroup(String groupId, String role);
    GroupCapacityResponseDTO getGroupCapacity(String groupId) throws BusinessException;
}
