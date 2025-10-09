package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import java.util.List;

public interface DeanService {
    DeanDTO createDean(DeanDTO deanDTO);
    DeanDTO updateDean(String id, DeanDTO deanDTO);
    void deleteDean(String id);
    DeanDTO getDeanById(String id);
    DeanDTO getDeanByEmployeeCode(String employeeCode);
    List<DeanDTO> getAllDeans();
    List<DeanDTO> getDeansByFaculty(String faculty);
    List<DeanDTO> getActiveDeans();
    List<DeanDTO> getDeansByProgram(String program);
    boolean existsByEmployeeCode(String employeeCode);
}