package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
public interface ProfessorService {
    ProfessorResponse createProfessor(ProfessorRequest professorRequest);
    ProfessorResponse getProfessorById(String id);
    ProfessorResponse getProfessorByCode(String code);
    ProfessorResponse getProfessorByEmail(String email);
    ProfessorResponse updateProfessor(String professorId, ProfessorRequest professorRequest);
    void deactivateProfessor(String professorId);
    List<ProfessorResponse> getProfessorsByFaculty(String facultyId);
    List<ProfessorResponse> getAllActiveProfessors();
    List<GroupResponse> getProfessorGroups(String professorId);
}
