package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.dto.ProfessorDTO;
import eci.edu.dosw.proyecto.dto.ProfessorPartialUpdateDTO;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.exception.ValidationException;
import eci.edu.dosw.proyecto.model.Professor;
import eci.edu.dosw.proyecto.repository.ProfessorRepository;
import eci.edu.dosw.proyecto.service.interfaces.ProfessorService;
import eci.edu.dosw.proyecto.utils.ProfessorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de profesores.
 * Contiene la lógica de negocio para crear, leer, actualizar y eliminar profesores,
 * aplicando permisos según el rol del usuario.
 */
@Service
public class ProfessorServiceImpl implements ProfessorService {

    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private ProfessorMapper mapper;

    /**
     * Crea un profesor.
     * Valida los campos obligatorios (nombre, identificación, correo institucional)
     * y marca al profesor como activo.
     */
    @Override
    public ProfessorDTO create(ProfessorDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("El nombre es obligatorio");
        }
        if (dto.getIdentification() == null || dto.getIdentification().isBlank()) {
            throw new ValidationException("La identificación es obligatoria");
        }
        if (dto.getInstitutionalEmail() == null || dto.getInstitutionalEmail().isBlank()) {
            throw new ValidationException("El correo institucional es obligatorio");
        }

        Professor p = mapper.toEntity(dto);
        p.setActive(true);
        repository.save(p);
        return mapper.toDto(p);
    }

    /**
     * Busca un profesor por ID.
     * Lanza NotFoundException si no existe o está inactivo.
     */
    @Override
    public ProfessorDTO findById(String id) {
        Professor p = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado"));
        if (!p.isActive()) {
            throw new NotFoundException("Profesor no encontrado");
        }
        return mapper.toDto(p);
    }

    /**
     * Busca un profesor por ID respetando permisos.
     * Los profesores solo pueden ver su propio perfil.
     */
    @Override
    public ProfessorDTO findByIdWithPermissions(String id, String requesterId) {
        Professor p = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado"));
        if (!p.isActive()) {
            throw new NotFoundException("Profesor no encontrado");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            boolean isProfessor = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ROLE_PROFESSOR") || a.equals("PROFESSOR"));

            if (isProfessor && (requesterId == null || !requesterId.equals(id))) {
                throw new BusinessException("No puede ver perfiles de otros profesores");
            }
        }
        return mapper.toDto(p);
    }

    /**
     * Lista todos los profesores activos.
     * Permite filtrar por facultad o por materia.
     */
    @Override
    public List<ProfessorDTO> findAll(String facultyId, String subjectId) {
        List<Professor> list;
        if (facultyId != null && !facultyId.isBlank()) {
            list = repository.findByFacultyIdAndActiveTrue(facultyId);
        } else if (subjectId != null && !subjectId.isBlank()) {
            list = repository.findBySubjectIdsContainingAndActiveTrue(subjectId);
        } else {
            list = repository.findByActiveTrue();
        }
        return list.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    /**
     * Actualiza todos los datos de un profesor (solo administrador).
     */
    @Override
    public ProfessorDTO updateAsAdmin(String id, ProfessorDTO dto) {
        Professor p = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado"));
        if (!p.isActive()) {
            throw new NotFoundException("Profesor no encontrado");
        }
        mapper.updateFromDto(dto, p);
        repository.save(p);
        return mapper.toDto(p);
    }

    /**
     * Actualiza campos permitidos del profesor (solo él mismo).
     * Solo puede cambiar email, teléfono y dirección.
     */
    @Override
    public ProfessorDTO updateSelf(String id, ProfessorPartialUpdateDTO dto, String authenticatedProfessorId) {
        if (!id.equals(authenticatedProfessorId)) {
            throw new BusinessException("No puede modificar otro perfil");
        }
        Professor p = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado"));
        if (!p.isActive()) {
            throw new NotFoundException("Profesor no encontrado");
        }
        mapper.partialUpdateFromDto(dto, p);
        repository.save(p);
        return mapper.toDto(p);
    }

    /**
     * Elimina un profesor marcándolo como inactivo.
     * Solo el administrador puede realizar esta acción.
     */
    @Override
    public void delete(String id) {
        Professor p = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado"));
        if (!p.isActive()) {
            return;
        }
        p.setActive(false);
        repository.save(p);
    }
}
