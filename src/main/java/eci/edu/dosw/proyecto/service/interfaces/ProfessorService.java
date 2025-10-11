package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.ProfessorDTO;
import eci.edu.dosw.proyecto.dto.ProfessorPartialUpdateDTO;

import java.util.List;

public interface ProfessorService {

    ProfessorDTO create(ProfessorDTO dto);

    ProfessorDTO findById(String id);

    /**
     * Busca un profesor verificando permisos (si el requester es professor no puede ver otros perfiles).
     */
    ProfessorDTO findByIdWithPermissions(String id, String requesterId);

    List<ProfessorDTO> findAll(String facultyId, String subjectId);

    ProfessorDTO updateAsAdmin(String id, ProfessorDTO dto);

    ProfessorDTO updateSelf(String id, ProfessorPartialUpdateDTO dto, String authenticatedProfessorId);

    void delete(String id);
}
