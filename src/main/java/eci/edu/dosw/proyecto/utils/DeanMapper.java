package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.DeanDTO;
import eci.edu.dosw.proyecto.dto.DeanPartialUpdateDTO;
import eci.edu.dosw.proyecto.model.Dean;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "active", ignore = true)
    Dean toEntity(DeanDTO dto);

    DeanDTO toDTO(Dean entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(DeanDTO dto, @MappingTarget Dean entity);

    /**
     * Método para actualizar solo campos específicos (útil para PATCH)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void partialUpdate(DeanPartialUpdateDTO dto, @MappingTarget Dean entity);
}