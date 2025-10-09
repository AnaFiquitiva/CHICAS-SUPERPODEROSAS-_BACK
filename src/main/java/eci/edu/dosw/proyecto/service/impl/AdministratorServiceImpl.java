package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.AdministratorDTO;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.utils.AdministratorMapper;
import eci.edu.dosw.proyecto.model.Administrator;
import eci.edu.dosw.proyecto.repository.AdministratorRepository;
import eci.edu.dosw.proyecto.service.interfaces.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministratorServiceImpl implements AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final AdministratorMapper administratorMapper;

    @Override
    public AdministratorDTO createAdministrator(AdministratorDTO administratorDTO) {
        Administrator administrator = administratorMapper.toEntity(administratorDTO);
        administrator.setCreatedDate(LocalDateTime.now());
        administrator.setActive(true);

        Administrator saved = administratorRepository.save(administrator);
        return administratorMapper.toDTO(saved);
    }

    @Override
    public AdministratorDTO updateAdministrator(String id, AdministratorDTO administratorDTO) {
        Administrator administrator = administratorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Administrador no encontrado"));

        administratorMapper.updateEntityFromDTO(administratorDTO, administrator);
        Administrator updated = administratorRepository.save(administrator);
        return administratorMapper.toDTO(updated);
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
        return administratorMapper.toDTO(administrator);
    }

    @Override
    public AdministratorDTO getAdministratorByEmployeeCode(String employeeCode) {
        Administrator administrator = administratorRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new NotFoundException("Administrador no encontrado"));
        return administratorMapper.toDTO(administrator);
    }

    @Override
    public List<AdministratorDTO> getAllAdministrators() {
        return administratorRepository.findAll().stream()
                .map(administratorMapper::toDTO)
                .toList();
    }

    @Override
    public List<AdministratorDTO> getAdministratorsByDepartment(String department) {
        return administratorRepository.findByDepartment(department).stream()
                .map(administratorMapper::toDTO)
                .toList();
    }

    @Override
    public List<AdministratorDTO> getActiveAdministrators() {
        return administratorRepository.findByActive(true).stream()
                .map(administratorMapper::toDTO)
                .toList();
    }

    @Override
    public boolean existsByEmployeeCode(String employeeCode) {
        return administratorRepository.existsByEmployeeCode(employeeCode);
    }
}