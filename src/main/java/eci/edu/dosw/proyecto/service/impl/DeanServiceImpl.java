package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.dto.DeanPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.CustomException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.model.RoleIdentifier;
import eci.edu.dosw.proyecto.repository.DeanRepository;
import eci.edu.dosw.proyecto.service.interfaces.DeanService;
import eci.edu.dosw.proyecto.utils.DeanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la gestión de Decanos.
 * - Mantiene los métodos originales para compatibilidad con funcionalidades existentes.
 * - Se agregan validaciones de duplicados y control de roles (Admin/Decano) según criterios de aceptación.
 */
@Service
@RequiredArgsConstructor
public class DeanServiceImpl implements DeanService {

    private final DeanRepository deanRepository;
    private final DeanMapper deanMapper;

    /**
     * Crear un nuevo decano.
     * - Solo Admin puede crear decanos.
     * - Valida duplicados por código de empleado y correo institucional.
     */
    @Override
    public DeanDTO createDean(DeanDTO deanDTO) {
        if (!RoleIdentifier.isAdmin()) {
            throw new CustomException("Solo el Administrador puede crear decanos");
        }

        if (deanRepository.existsByEmployeeCode(deanDTO.getEmployeeCode())) {
            throw new CustomException("Ya existe un decano con este código de empleado");
        }
        if (deanRepository.existsByEmail(deanDTO.getInstitutionalEmail())) {
            throw new CustomException("Ya existe un decano con este correo institucional");
        }

        Dean dean = deanMapper.toEntity(deanDTO);
        dean.setCreatedDate(LocalDateTime.now());
        dean.setActive(true);

        Dean saved = deanRepository.save(dean);
        return deanMapper.toDTO(saved);
    }

    /**
     * Actualizar completamente un decano existente.
     * Solo Admin puede actualizar.
     */
    @Override
    public DeanDTO updateDean(String id, DeanDTO deanDTO) {
        if (!RoleIdentifier.isAdmin()) {
            throw new CustomException("Solo el Administrador puede actualizar decanos");
        }

        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));

        deanMapper.updateEntityFromDTO(deanDTO, dean);
        Dean updated = deanRepository.save(dean);
        return deanMapper.toDTO(updated);
    }

    /**
     * Desactivar un decano (delete lógico).
     * Solo Admin puede eliminar.
     */
    @Override
    public void deleteDean(String id) {
        if (!RoleIdentifier.isAdmin()) {
            throw new CustomException("Solo el Administrador puede eliminar decanos");
        }

        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));
        dean.setActive(false);
        deanRepository.save(dean);
    }

    /**
     * Obtener un decano por ID.
     * - Admin puede ver cualquier decano.
     * - Decano solo puede ver su propia información.
     */
    @Override
    public DeanDTO getDeanById(String id) {
        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));

        if (RoleIdentifier.isDean() &&
                !dean.getEmployeeCode().equals(RoleIdentifier.getCurrentUserEmail())) {
            throw new CustomException("No tiene permiso para ver la información de este decano");
        }

        return deanMapper.toDTO(dean);
    }

    /**
     * Obtener un decano por código de empleado.
     * - Admin puede ver cualquier decano.
     * - Decano solo puede ver su propia información.
     */
    @Override
    public DeanDTO getDeanByEmployeeCode(String employeeCode) {
        Dean dean = deanRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));

        if (RoleIdentifier.isDean() &&
                !dean.getEmployeeCode().equals(RoleIdentifier.getCurrentUserEmail())) {
            throw new CustomException("No tiene permiso para ver la información de este decano");
        }

        return deanMapper.toDTO(dean);
    }

    /**
     * Obtener todos los decanos.
     * - Admin ve todos.
     * - Decano solo su propio registro.
     */
    @Override
    public List<DeanDTO> getAllDeans() {
        List<Dean> deans = deanRepository.findAll();

        if (RoleIdentifier.isDean()) {
            String currentEmail = RoleIdentifier.getCurrentUserEmail();
            deans = deans.stream()
                    .filter(d -> d.getEmployeeCode().equals(currentEmail))
                    .collect(Collectors.toList());
        }

        return deans.stream()
                .map(deanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener decanos por facultad.
     * - Admin puede ver cualquier facultad.
     * - Decano solo puede ver su propia facultad.
     */
    @Override
    public List<DeanDTO> getDeansByFaculty(String faculty) {
        if (RoleIdentifier.isDean()) {
            String currentEmail = RoleIdentifier.getCurrentUserEmail();
            Dean dean = deanRepository.findByEmployeeCode(currentEmail)
                    .orElseThrow(() -> new CustomException("Decano no autorizado"));
            faculty = dean.getFaculty();
        }

        return deanRepository.findByFaculty(faculty).stream()
                .map(deanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener decanos activos.
     * - Admin ve todos activos.
     * - Decano solo su propio registro.
     */
    @Override
    public List<DeanDTO> getActiveDeans() {
        List<Dean> deans = deanRepository.findByActive(true);

        if (RoleIdentifier.isDean()) {
            String currentEmail = RoleIdentifier.getCurrentUserEmail();
            deans = deans.stream()
                    .filter(d -> d.getEmployeeCode().equals(currentEmail))
                    .collect(Collectors.toList());
        }

        return deans.stream()
                .map(deanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener decanos por programa.
     * - Admin puede ver cualquier programa.
     * - Decano solo puede ver los programas que maneja.
     */
    @Override
    public List<DeanDTO> getDeansByProgram(String program) {
        List<Dean> deans = deanRepository.findByProgramsContaining(program);

        if (RoleIdentifier.isDean()) {
            String currentEmail = RoleIdentifier.getCurrentUserEmail();
            deans = deans.stream()
                    .filter(d -> d.getEmployeeCode().equals(currentEmail))
                    .collect(Collectors.toList());
        }

        return deans.stream()
                .map(deanMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si existe un decano por código de empleado.
     */
    @Override
    public boolean existsByEmployeeCode(String employeeCode) {
        return deanRepository.existsByEmployeeCode(employeeCode);
    }

    /**
     * Actualiza parcialmente un decano (PATCH).
     * - Solo Admin puede modificar campos específicos.
     */
    @Override
    public DeanDTO updateDeanPartial(String id, DeanPartialUpdateDTO partialDTO) {
        if (!RoleIdentifier.isAdmin()) {
            throw new CustomException("Solo el Administrador puede actualizar decanos");
        }

        Dean dean = deanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Decano no encontrado"));

        deanMapper.partialUpdate(partialDTO, dean);
        Dean updated = deanRepository.save(dean);
        return deanMapper.toDTO(updated);
    }
}

