package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.dto.AdministratorDTO;
import eci.edu.dosw.proyecto.model.Administrator;

import java.util.List;

public interface AdministratorService {
    AdministratorDTO createAdministrator(AdministratorDTO administratorDTO);
    AdministratorDTO updateAdministrator(String id, AdministratorDTO administratorDTO);
    void deleteAdministrator(String id);
    AdministratorDTO getAdministratorById(String id);
    AdministratorDTO getAdministratorByEmployeeCode(String employeeCode);
    List<AdministratorDTO> getAllAdministrators();
    List<AdministratorDTO> getAdministratorsByDepartment(String department);
    List<AdministratorDTO> getActiveAdministrators();
    boolean existsByEmployeeCode(String employeeCode);
}