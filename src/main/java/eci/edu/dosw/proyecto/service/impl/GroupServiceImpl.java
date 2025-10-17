package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.repository.GroupRepository;
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
    private final GroupMapper groupMapper;

    @Override
    public GroupCapacityResponseDTO getCapacity(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        return groupMapper.toCapacityDTO(group);
    }

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
        List<Group> subjectGroups = groupRepository.findBySubjectId(subjectId);
        return subjectGroups.stream()
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
}