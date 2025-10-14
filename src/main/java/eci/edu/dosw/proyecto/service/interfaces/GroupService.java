package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.GroupCapacityResponseDTO;

public interface GroupService {
    /**
     * Obtiene el estado del cupo del grupo indicado.
     */
    GroupCapacityResponseDTO getCapacity(String groupId);
}
