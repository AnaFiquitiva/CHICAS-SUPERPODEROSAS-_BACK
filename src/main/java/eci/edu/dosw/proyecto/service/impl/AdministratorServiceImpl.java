package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.AdministratorDTO;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Administrator;
import eci.edu.dosw.proyecto.repository.AdministratorRepository;
import eci.edu.dosw.proyecto.service.interfaces.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministratorServiceImpl implements AdministratorService {

    private final AdministratorRepository administratorRepository;

    @Override
    public AdministratorDTO createAdministrator(AdministratorDTO administratorDTO) {
        Administrator administrator = new Administrator();
        mapDTOToEntity(administratorDTO, administrator);
        administrator.setCreatedDate(LocalDateTime.now());
        administrator.setActive(true);

        Administrator saved = administratorRepository.save(administrator);
        return mapEntityToDTO(saved);
    }

    @Override
    public AdministratorDTO updateAdministrator(String id, AdministratorDTO administratorDTO) {
        Administrator administrator = administratorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Administrador no encontrado"));

        mapDTOToEntity(administratorDTO, administrator);
        Administrator updated = administratorRepository.save(administrator);
        return mapEntityToDTO(updated);
    }

    @Override
    public void deleteAdministrator(String id) {
        Administrator administrator = administratorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Administrador no encontrado"));
        administrator.setActive(false);
        administratorRepository.save(administrator);
    }

    @Override
    public AdministratorDTO getAdministratorById(String id) {
        Administrator administrator = administratorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Administrador no encontrado"));
        return mapEntityToDTO(administrator);
    }

    @Override
    public AdministratorDTO getAdministratorByEmployeeCode(String employeeCode) {
        Administrator administrator = administratorRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new NotFoundException("Administrador no encontrado"));
        return mapEntityToDTO(administrator);
    }

    @Override
    public List<AdministratorDTO> getAllAdministrators() {
        return administratorRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdministratorDTO> getAdministratorsByDepartment(String department) {
        return administratorRepository.findByDepartment(department).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdministratorDTO> getActiveAdministrators() {
        return administratorRepository.findByActive(true).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmployeeCode(String employeeCode) {
        return administratorRepository.existsByEmployeeCode(employeeCode);
    }

    private void mapDTOToEntity(AdministratorDTO dto, Administrator entity) {
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setInstitutionalEmail(dto.getInstitutionalEmail());
        entity.setDepartment(dto.getDepartment());
        entity.setPosition(dto.getPosition());
        entity.setType(dto.getType());
        entity.setPermissions(dto.getPermissions());
        entity.setActive(dto.getActive());
        entity.setCanManageStudents(dto.getCanManageStudents());
        entity.setCanManageRequests(dto.getCanManageRequests());
        entity.setCanManageAcademicPeriods(dto.getCanManageAcademicPeriods());
        entity.setCanGenerateReports(dto.getCanGenerateReports());
        entity.setCanManageSystemConfig(dto.getCanManageSystemConfig());
    }

    private AdministratorDTO mapEntityToDTO(Administrator entity) {
        AdministratorDTO dto = new AdministratorDTO();
        dto.setId(entity.getId());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setInstitutionalEmail(entity.getInstitutionalEmail());
        dto.setDepartment(entity.getDepartment());
        dto.setPosition(entity.getPosition());
        dto.setType(entity.getType());
        dto.setPermissions(entity.getPermissions());
        dto.setActive(entity.getActive());
        dto.setCanManageStudents(entity.getCanManageStudents());
        dto.setCanManageRequests(entity.getCanManageRequests());
        dto.setCanManageAcademicPeriods(entity.getCanManageAcademicPeriods());
        dto.setCanGenerateReports(entity.getCanGenerateReports());
        dto.setCanManageSystemConfig(entity.getCanManageSystemConfig());
        return dto;
    }
}