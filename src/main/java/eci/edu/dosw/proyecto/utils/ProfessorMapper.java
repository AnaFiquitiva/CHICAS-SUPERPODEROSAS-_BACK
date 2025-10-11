package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.ProfessorDTO;
import eci.edu.dosw.proyecto.dto.ProfessorPartialUpdateDTO;
import eci.edu.dosw.proyecto.model.Professor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper de profesores.
 * Convierte entre las entidades Professor y sus DTOs (ProfesorDTO y ProfessorPartialUpdateDTO).
 * Utiliza MapStruct para la generación automática de código de mapeo.
 */
@Mapper(componentModel = "spring")
public interface ProfessorMapper {

    /**
     * Convierte un objeto Professor a ProfessorDTO.
     * Incluye todos los campos.
     */
    ProfessorDTO toDto(Professor professor);

    /**
     * Convierte un objeto ProfessorDTO a Professor.
     * Ignora valores nulos en el DTO para no sobrescribir campos existentes.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Professor toEntity(ProfessorDTO dto);

    /**
     * Actualiza todos los campos de la entidad Professor desde un DTO.
     * Ignora valores nulos en el DTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(ProfessorDTO dto, @MappingTarget Professor entity);

    /**
     * Actualiza solo los campos permitidos de la entidad Professor desde un DTO parcial.
     * Se utiliza para que un profesor solo modifique email, teléfono y dirección.
     * Ignora valores nulos en el DTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ProfessorPartialUpdateDTO dto, @MappingTarget Professor entity);
}
