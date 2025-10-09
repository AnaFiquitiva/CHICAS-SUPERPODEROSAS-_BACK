package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.utils.DeanMapper;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeanServiceImpl implements DeanService {

    private final DeanRepository deanRepository;
    private final DeanMapper deanMapper;

    @Override
    public DeanDTO createDean(DeanDTO deanDTO) {
        Dean dean = deanMapper.toEntity(deanDTO);
        dean.setCreatedDate(LocalDateTime.now());
        dean.setActive(true);

        Dean saved = deanRepository.save(dean);
        return deanMapper.toDTO(saved);
    }

    @Override
    public DeanDTO updateDean(String id, DeanDTO deanDTO) {
        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));

        deanMapper.updateEntityFromDTO(deanDTO, dean);
        Dean updated = deanRepository.save(dean);
        return deanMapper.toDTO(updated);
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
        return deanMapper.toDTO(dean);
    }

    @Override
    public DeanDTO getDeanByEmployeeCode(String employeeCode) {
        Dean dean = deanRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));
        return deanMapper.toDTO(dean);
    }

    @Override
    public List<DeanDTO> getAllDeans() {
        return deanRepository.findAll().stream()
                .map(deanMapper::toDTO)
                .toList();
    }

    @Override
    public List<DeanDTO> getDeansByFaculty(String faculty) {
        return deanRepository.findByFaculty(faculty).stream()
                .map(deanMapper::toDTO)
                .toList();
    }

    @Override
    public List<DeanDTO> getActiveDeans() {
        return deanRepository.findByActive(true).stream()
                .map(deanMapper::toDTO)
                .toList();
    }

    @Override
    public List<DeanDTO> getDeansByProgram(String program) {
        return deanRepository.findByProgramsContaining(program).stream()
                .map(deanMapper::toDTO)
                .toList();
    }

    @Override
    public boolean existsByEmployeeCode(String employeeCode) {
        return deanRepository.existsByEmployeeCode(employeeCode);
    }
}