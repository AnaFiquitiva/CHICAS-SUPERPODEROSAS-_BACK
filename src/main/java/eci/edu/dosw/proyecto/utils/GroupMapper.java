// GroupMapper.java - VERSIÓN CORREGIDA
package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;
import eci.edu.dosw.proyecto.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupCapacityResponseDTO toCapacityDTO(Group group) {
        if (group == null) {
            return null;
        }

        // Usar el constructor por defecto y setters
        GroupCapacityResponseDTO dto = new GroupCapacityResponseDTO();
        dto.setGroupId(group.getId());
        dto.setGroupCode(group.getGroupCode());
        dto.setCurrentEnrollment(group.getCurrentEnrollment());
        dto.setMaxCapacity(group.getMaxCapacity());
        dto.setAvailableSpaces(calculateAvailableSpaces(group));
        dto.setWaitlistCount(group.getWaitingListCount());
        dto.setOccupancyRate(calculateOccupancyRate(group));
        dto.setStatus(determineStatus(group));
        dto.setSubjectName(getSubjectName(group.getSubjectId())); // Necesitarás implementar esto

        return dto;
    }

    private Integer calculateAvailableSpaces(Group group) {
        return group.getMaxCapacity() - group.getCurrentEnrollment();
    }

    private Double calculateOccupancyRate(Group group) {
        if (group.getMaxCapacity() == null || group.getMaxCapacity() == 0) {
            return 0.0;
        }
        return (double) group.getCurrentEnrollment() / group.getMaxCapacity() * 100;
    }

    private String determineStatus(Group group) {
        Integer availableSpaces = calculateAvailableSpaces(group);
        if (availableSpaces <= 0) {
            return "FULL";
        } else if (availableSpaces <= 2) {
            return "LIMITED";
        } else {
            return "AVAILABLE";
        }
    }

    private String getSubjectName(String subjectId) {
        // Implementar lógica para obtener el nombre de la materia

        return "Subject-" + subjectId;
    }
}