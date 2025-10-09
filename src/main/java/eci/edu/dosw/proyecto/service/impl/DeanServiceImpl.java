package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanServiceImpl implements DeanService {

    private final DeanRepository deanRepository;

    @Override
    public DeanDTO createDean(DeanDTO deanDTO) {
        Dean dean = new Dean();
        mapDTOToEntity(deanDTO, dean);
        dean.setCreatedDate(LocalDateTime.now());
        dean.setActive(true);

        Dean saved = deanRepository.save(dean);
        return mapEntityToDTO(saved);
    }

    @Override
    public DeanDTO updateDean(String id, DeanDTO deanDTO) {
        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));

        mapDTOToEntity(deanDTO, dean);
        Dean updated = deanRepository.save(dean);
        return mapEntityToDTO(updated);
    }

    @Override
    public void deleteDean(String id) {
        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));
        dean.setActive(false);
        deanRepository.save(dean);
    }

    @Override
    public DeanDTO getDeanById(String id) {
        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));
        return mapEntityToDTO(dean);
    }

    @Override
    public DeanDTO getDeanByEmployeeCode(String employeeCode) {
        Dean dean = deanRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));
        return mapEntityToDTO(dean);
    }

    @Override
    public List<DeanDTO> getAllDeans() {
        return deanRepository.findAll().stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeanDTO> getDeansByFaculty(String faculty) {
        return deanRepository.findByFaculty(faculty).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeanDTO> getActiveDeans() {
        return deanRepository.findByActive(true).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeanDTO> getDeansByProgram(String program) {
        return deanRepository.findByProgramsContaining(program).stream()
                .map(this::mapEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmployeeCode(String employeeCode) {
        return deanRepository.existsByEmployeeCode(employeeCode);
    }

    private void mapDTOToEntity(DeanDTO dto, Dean entity) {
        entity.setEmployeeCode(dto.getEmployeeCode());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setInstitutionalEmail(dto.getInstitutionalEmail());
        entity.setFaculty(dto.getFaculty());
        entity.setPrograms(dto.getPrograms());
        entity.setType(dto.getType());
        entity.setActive(dto.getActive());
        entity.setCanApprovePlanChanges(dto.getCanApprovePlanChanges());
        entity.setCanViewAllRequests(dto.getCanViewAllRequests());
        entity.setCanManageFaculty(dto.getCanManageFaculty());
        entity.setCanApproveSpecialRequests(dto.getCanApproveSpecialRequests());
        entity.setCanGenerateFacultyReports(dto.getCanGenerateFacultyReports());
    }

    private DeanDTO mapEntityToDTO(Dean entity) {
        DeanDTO dto = new DeanDTO();
        dto.setId(entity.getId());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setInstitutionalEmail(entity.getInstitutionalEmail());
        dto.setFaculty(entity.getFaculty());
        dto.setPrograms(entity.getPrograms());
        dto.setType(entity.getType());
        dto.setActive(entity.getActive());
        dto.setCanApprovePlanChanges(entity.getCanApprovePlanChanges());
        dto.setCanViewAllRequests(entity.getCanViewAllRequests());
        dto.setCanManageFaculty(entity.getCanManageFaculty());
        dto.setCanApproveSpecialRequests(entity.getCanApproveSpecialRequests());
        dto.setCanGenerateFacultyReports(entity.getCanGenerateFacultyReports());
        return dto;
    }
}