package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.dto.WaitingListEntryDTO;
import java.util.List;

/**
 * Servicio para consultar la lista de espera de un grupo.
 */
public interface WaitingListService {
    List<WaitingListEntryDTO> getWaitingListByGroup(String groupId, String userRole, String userFaculty);
}
