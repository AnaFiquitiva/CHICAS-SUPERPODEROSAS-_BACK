package eci.edu.dosw.proyecto.service.impl;


import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.exception.GroupNotFoundException;
import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.repository.GroupRepository;
import eci.edu.dosw.proyecto.service.interfaces.GroupService;
import eci.edu.dosw.proyecto.utils.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    @Override
    public GroupCapacityResponseDTO getCapacity(String groupId) {
        Optional<Group> optGroup = groupRepository.findById(groupId);
        if (!optGroup.isPresent()) {
            throw new GroupNotFoundException("Grupo no encontrado con el id: " + groupId);
        }
        Group group = optGroup.get();
        return groupMapper.toCapacityDTO(group);
    }
}
