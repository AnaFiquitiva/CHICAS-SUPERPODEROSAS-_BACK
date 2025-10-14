package eci.edu.dosw.proyecto.utils;


import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.model.Group;
import org.springframework.stereotype.Component;

/**
 * Mapper simple para convertir Group -> GroupCapacityResponseDTO
 */
@Component
public class GroupMapper {

    public GroupCapacityResponseDTO toCapacityDTO(Group group) {
        Integer enrolled = group.getCurrentEnrollment() != null ? group.getCurrentEnrollment() : 0;
        Integer max = group.getMaxCapacity() != null ? group.getMaxCapacity() : 0;

        double percent = 0.0;
        if (max != 0) {
            percent = ((double) enrolled / (double) max) * 100.0;
        }


        percent = Math.round(percent * 100.0) / 100.0;

        return new GroupCapacityResponseDTO(group.getId(), enrolled, max, percent);
    }
}
