package eci.edu.dosw.proyecto.utils;

import eci.edu.dosw.proyecto.dto.AdministratorDTO;
import eci.edu.dosw.proyecto.model.Administrator;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdministratorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Administrator toEntity(AdministratorDTO dto);

    AdministratorDTO toDTO(Administrator entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    void updateEntityFromDTO(AdministratorDTO dto, @MappingTarget Administrator entity);
}